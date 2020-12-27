package archive

sealed trait Compression

case class Undetectable() extends Compression
case class Name( name :String) extends Compression