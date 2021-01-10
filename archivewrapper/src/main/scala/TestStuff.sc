import scala.collection.mutable.Queue

class Input(queue: Queue[Int]) extends AutoCloseable {
  private val q = queue
  private var closed = false
  def close() = { 
     closed = true
     println("closing input")
  }
  def take() : Int = {
    if(closed) throw new Exception("its closed")
    q.dequeue()
  }
}

class Output(id: Int) (implicit manager: Using.Manager) extends AutoCloseable {
  private var closed = false
  manager.acquire(this)
  def close() = {
      closed = true
      println(id)
  }
  def doStuff(i: Int) = { 
    if(closed) throw new Exception("its closed")
  }
}


val in = new Input(Queue(1,2,3))
val outs = List(new Output(), new Output(), new Output())


// object Main extends App {
//     println("Hello, world")
// }