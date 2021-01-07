package archive

import scala.collection.immutable.HashMap
import java.io.File

sealed trait Compression

case class Undetectable() extends Compression
case class Name( name :String) extends Compression

// ArchiveEntry
// ArchiveOutputStream


//Input
sealed trait Structure
case class ArchiveStructure(name: String, value:  HashMap[ArchiveFile,Structure]) extends Structure
case class ArchiveFile(name:String) extends Structure

//tree reversed pointer Output, the mapping are from archive to archive/file and root is always a File
sealed trait Tree
case class ArchiveNode[A](name: String, parent : Tree, leafs: HashMap[Leaf, A], output: Option[A]) extends Tree
case class Leaf(name: String)
case class Root(filename: String, output: Option[File]) extends Tree

case class Path( path: Seq[Tree])



//  (ArchiveEntry, InputStream)