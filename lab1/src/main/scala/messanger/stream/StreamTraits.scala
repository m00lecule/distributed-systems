package messanger.stream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

trait StreamObjectOperations extends AutoCloseable {

  val socket: Socket

  def writeObject(message: AnyRef) = new ObjectOutputStream(socket.getOutputStream).writeObject(message)

  def readObjectAndSender = (readObject, socket)

  def readObject = new ObjectInputStream(socket.getInputStream).readObject

  override def close = socket.close
}
