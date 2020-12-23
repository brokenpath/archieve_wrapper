package archive

import java.io.{BufferedInputStream, InputStream}

import org.apache.commons.compress.archivers.{
  ArchiveEntry,
  ArchiveException,
  ArchiveInputStream,
  ArchiveStreamFactory
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

  //Split should be changed to decompress, and getIter, where getIter should return only a single "element" if things are not an archieve
  //decompress should on right return a Wrapped bufferedstream if its compressed or if its an archieve like zip else it should use left for errors 
  // "not sure we can even detect if its a error of decompressor or archive so i think following the happy path is the way to go"


  def open(
    inputStream: InputStream
  ): Either[UnarchiverError, Iterator[(ArchiveEntry, InputStream)]] =
  {
    val in = new BufferedInputStream(inputStream)
    val uncompressedStream : Either[UnarchiverError, InputStream] = uncompress(in) match {
      case Right(input) => Right(input)
      case Left(err : CompressorError) => Right(in) //could be its an archive like tar or zip without compression
      case err => err 
    }
    for {
      stream <- uncompressedStream
      archiveInputStream <- extract(stream)
      iterator = createIterator(archiveInputStream)
    } yield iterator
  }

  def iterate( inputStream: InputStream): Either[UnarchiverError, Iterator[(ArchiveEntry, InputStream)]] =
    for {
      archiveInputStream <- extract(inputStream)
      iterator = createIterator(archiveInputStream)
    } yield iterator

    
  private def createIterator(
    archiveInputStream: ArchiveInputStream
  ): Iterator[(ArchiveEntry, InputStream)] =
    new Iterator[(ArchiveEntry, InputStream)] {
      private var latest: ArchiveEntry = _

      override def hasNext: Boolean = {
        latest = archiveInputStream.getNextEntry
        latest != null
      }

      override def next(): (ArchiveEntry, InputStream) =
        (latest, new CloseShieldInputStream(archiveInputStream))
    }

    //Throws exceptions in case stream is "bad"
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
  ): Either[UnarchiverError, InputStream] = 
    {
      Try {
        // We have to wrap in a BufferedInputStream because this method
        // only takes InputStreams that support the `mark()` method.
        // val bufferedstream = new BufferedInputStream(compressedStream)
        new CompressorStreamFactory()
            .createCompressorInputStream(compressedStream)
        } match {
          case Success(stream)                   => Right(stream)
          case Failure(err: CompressorException) => Right(compressedStream) //swallow "error" and hope its just because its a tar or a zip.
          case Failure(err)                      => Left(UnexpectedUnarchiverError(err))
        }
    }

  def extract(
    inputStream: InputStream
  ): Either[UnarchiverError, ArchiveInputStream] =
    Try {
      // We have to wrap in a BufferedInputStream because this method
      // only takes InputStreams that support the `mark()` method.
      new ArchiveStreamFactory()
        .createArchiveInputStream(new BufferedInputStream(inputStream))
    } match {
      case Success(stream)                => Right(stream)
      case Failure(err: ArchiveException) => Left(ArchiveFormatError(err))
      case Failure(err)                   => Left(UnexpectedUnarchiverError(err))
    }
}