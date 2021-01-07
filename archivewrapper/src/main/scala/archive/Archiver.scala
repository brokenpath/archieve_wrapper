package archive

import org.apache.commons.compress.archivers.ArchiveEntry
import java.io.InputStream
import org.apache.commons.compress.archivers.ArchiveOutputStream
import scala.util.Try
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import java.nio.file.attribute.FileTime
import org.apache.commons.compress.utils.IOUtils

object Archiver {
    
    case class Leaf(name: String)
    case class OutPutArchive()
    case class ArMapping(inputFileName: String, outputFileName: String, rootArchive: String)

    def repack(file: ArFile, l: Leaf, os: ArchiveOutputStream) : Try[Unit] = Try {
        val entry = new ZipArchiveEntry(l.name)
        val time = FileTime.from(entry.getLastModifiedDate().toInstant())
        entry.setLastModifiedTime(time)
        entry.setSize(file.entry.getSize())
        os.putArchiveEntry(entry)
        IOUtils.copy(file.input, os)
        os.closeArchiveEntry()
    }

    
    def copy(archivePath: String, mapping : Seq[ArMapping] ) = {
        


    }


  
}
