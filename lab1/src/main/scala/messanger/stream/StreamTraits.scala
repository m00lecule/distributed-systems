package messanger.stream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

trait StreamObjectOperations extends AutoCloseable {

  val socket: Socket

  def writeObject(message: AnyRef) = {
    val output = new ObjectOutputStream(socket.getOutputStream)
    output.writeObject(message)
  }

  def readObjectAndSender = (readObject, socket)

  def readObject = {
    val input = new ObjectInputStream(socket.getInputStream)
    input.readObject()
  }

  override def close = socket.close
}
