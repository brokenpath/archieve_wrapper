package archive

import java.io.{BufferedInputStream, InputStream}

import org.apache.commons.compress.archivers.{
  ArchiveEntry,
  ArchiveException,
  ArchiveInputStream,
  ArchiveStreamFactory,
  ArchiveOutputStream
}
import org.apache.commons.compress.compressors.{
  CompressorException,
  CompressorInputStream,
  CompressorStreamFactory
}
import org.apache.commons.io.input.CloseShieldInputStream

import scala.util.{Failure, Success, Try}

/** You pass this class an inputStream that comes from the storage provider,
  * e.g. a FileInputStream or an S3InputStream.  The stream should be a tar.gz
  * or similar container + compression format.
  *
  * The iterator it produces has two pieces:
  *  1. ArchiveEntry -- metadata about the original file, e.g. name/size
  *  2. InputStream -- a stream that gives you the content for this file
  *
  * This InputStream is really just a view into the original stream.
  * If you advance one, the other advances.  They move together.
  *
  *       +------------------------------------------+ original stream
  *
  *       +-------+                                    file1
  *               +------------------+                 file2
  *                                  +---------+       file3
  *                                            +-----+ file4
  *
  * This is why we wrap the output in a close shield: if the caller closed
  * the individual stream, they'd close the underlying stream and we'd be
  * unable to read any more of the archive.
  *
  */
object Unarchiver {

  def open(
    inputStream: InputStream
  ): Try[Iterator[ArFile]] =
  {
    val in = new BufferedInputStream(inputStream)
    for {
      compression <- detectCompression(in)
      stream <- compression match {
        case Undetectable() => Try { in }
        case Name(name) => uncompress(in)
      } 
      archiveInputStream <- extract(stream)
      iterator = createIterator(archiveInputStream)
    } yield iterator
  }

    
  private def createIterator(
    archiveInputStream: ArchiveInputStream
  ): Iterator[ArFile] =
    new Iterator[ArFile] {
      private var latest: ArchiveEntry = _

      override def hasNext: Boolean = {
        latest = archiveInputStream.getNextEntry
        latest != null
      }

      override def next(): ArFile =
        ArFile(latest, new CloseShieldInputStream(archiveInputStream))
    }

  def detectCompression(stream: BufferedInputStream) : Try[Compression] = 
    Try {
      CompressorStreamFactory.detect(stream)
    } match {
      case Success(compressionName) => Success(Name(compressionName))
      case Failure(err: CompressorException) 
        if(err.getMessage().startsWith("No Compressor found")) => Success(Undetectable()) 
      case Failure(err) => Failure(err)
    }

  def uncompress(
    compressedStream: BufferedInputStream
  ): Try[InputStream] = 
    {
      Try {
        new CompressorStreamFactory()
            .createCompressorInputStream(compressedStream)
        } 
    }

  def extract(
    inputStream: InputStream
  ): Try[ArchiveInputStream] =
    Try {
      // We have to wrap in a BufferedInputStream because this method
      // only takes InputStreams that support the `mark()` method.
      new ArchiveStreamFactory()
        .createArchiveInputStream(new BufferedInputStream(inputStream))
    }
}