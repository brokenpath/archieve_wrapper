import munit._
import archive.Unarchiver
import java.io.BufferedInputStream
import ammonite.util.Res.Success
import archive.Name


// Archive:  file.zip
//   Length      Date    Time    Name
// ---------  ---------- -----   ----
//        15  2020-12-16 20:20   file1.txt
//        20  2020-12-16 20:20   file2.txt
// ---------                     -------
//        35                     2 files

class UnarchiveSuite extends FunSuite {
  

test("readArchiveStraight"){
   val stream = new BufferedInputStream(getClass.getResourceAsStream("/file.zip"))
   val result = Unarchiver.detectCompression(stream)
   assert(result.isSuccess)
   assertEquals(result.get,Name("zip"))
}

}
