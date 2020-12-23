import munit._


// Archive:  file.zip
//   Length      Date    Time    Name
// ---------  ---------- -----   ----
//        15  2020-12-16 20:20   file1.txt
//        20  2020-12-16 20:20   file2.txt
// ---------                     -------
//        35                     2 files

class UnarchiveSuite extends FunSuite {
   test("hello") {
      val obtained = 42
      val expected = 43
      assertEquals(obtained, expected)
  }
}
