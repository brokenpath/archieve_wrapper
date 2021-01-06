import munit._
import archive.Unarchiver
import java.io.BufferedInputStream
import archive.Name
import archive.Undetectable
import scala.util.Failure


// Archive:  file.zip
//   Length      Date    Time    Name
// ---------  ---------- -----   ----
//        15  2020-12-16 20:20   file1.txt
//        20  2020-12-16 20:20   file2.txt
// ---------                     -------
//        35                     2 files

class UnarchiveSuite extends FunSuite {
  

   test("readArchiveasCompression"){
      val stream = new BufferedInputStream(getClass.getResourceAsStream("/file.zip"))
      val result = Unarchiver.detectCompression(stream)
      assert(result.isSuccess)
      assertEquals(result.get,Undetectable())
   }

   test("openZipArchive"){
      val stream = getClass.getResourceAsStream("/file.zip")
      val result = Unarchiver.open(stream)
      assert(result.isSuccess)
      val expectedEntries = List(("file1.txt", 15l), ("file2.txt",20l))
      val archiveEntries = result match {
         case scala.util.Failure(exception) => List()
         case scala.util.Success(value) => value.toList.map(
            t2 => (t2._1.getName(), t2._1.getSize()))
      }
      assertEquals(archiveEntries, expectedEntries)
   }
}
