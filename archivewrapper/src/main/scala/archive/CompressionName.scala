package archive

import scala.collection.immutable.HashMap

sealed trait Compression

case class Undetectable() extends Compression
case class Name( name :String) extends Compression




//Input
sealed trait Structure
case class ArchiveStructure(name: String, value:  HashMap[ArchiveFile,Structure]) extends Structure
case class ArchiveFile(name:String) extends Structure

//Output
sealed trait Tree
case class ArchiveNode[A](name: String, parent : Tree, output: Option[A]) extends Tree
case class Root[A](filename: String, output: Option[A]) extends Tree

case class Path( path: Seq[Tree])
