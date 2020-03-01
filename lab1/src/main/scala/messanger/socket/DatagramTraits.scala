package messanger.socket

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{DatagramPacket, DatagramSocket, InetAddress}

trait DatagramObjectOperations[A <: DatagramSocket] extends AutoCloseable {
  val socket: A
  val receive = new Array[Byte](65535)
  val packet = new DatagramPacket(receive, receive.length);

  def readObjectAndSender = (readObject, (packet.getPort, packet.getAddress))

  def sendObjectTo(message: AnyRef, recipient: (Int, InetAddress)) = {
    val byteOut = new ByteArrayOutputStream
    new ObjectOutputStream(byteOut).writeObject(message)
    socket.send(new DatagramPacket(byteOut.toByteArray, byteOut.toByteArray.length, recipient._2, recipient._1))
  }

  def readObject = {
    socket.receive(packet)
    new ObjectInputStream(new ByteArrayInputStream(packet.getData)).readObject()
  }

  override def close() = socket.close
}