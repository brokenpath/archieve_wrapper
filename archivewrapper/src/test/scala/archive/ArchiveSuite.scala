package archive


import munit._
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
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.utils.IOUtils
import java.io.File


class ArchiveSuite extends FunSuite {
    def getFile(path: String) : BufferedInputStream = new BufferedInputStream(new FileInputStream(path))


    test("writeArchive"){
        val v = Try {
            val f1 = getFile("/home/maovmini/data/file1.txt")
            val f2 = getFile("/home/maovmini/data/file2.txt")
            val outputPath = "/home/maovmini/data/output.tar"
            val outputFile = new FileOutputStream(outputPath)
            var taros = new TarArchiveOutputStream(outputFile)
            
            val os = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, outputFile);
            val entry = new TarArchiveEntry("file1.txt")

            entry.setSize(6l) //size is needed for header
            os.putArchiveEntry(entry)
            print(IOUtils.copy(f1, os))
            os.closeArchiveEntry()
            os.finish()
            taros.close()


        } match {
            case Success(compressionName) => "Yess"
            case Failure(err)  => err.fillInStackTrace().getMessage()
        }

        print(v)





    }
  
}
