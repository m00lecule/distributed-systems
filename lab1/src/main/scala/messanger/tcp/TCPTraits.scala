package messanger.tcp

import java.io.{InputStream, ObjectInputStream}

trait TCPRead {
  val inputSocketStream: InputStream;
  val input = new ObjectInputStream(inputSocketStream)

  def readObject = input.readObject()
}
