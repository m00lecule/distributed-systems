package messanger

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.net.{DatagramPacket, DatagramSocket}

class UDPEndPoint(val socket: DatagramSocket) {

  val receive = new Array[Byte](65535)
  val packet = new DatagramPacket(receive, receive.length);

  def this() = this(new DatagramSocket)
}

trait UDPRead {
  val packet: DatagramPacket
  val socket: DatagramSocket

  def readObject = {
    socket.receive(packet)
    new ObjectInputStream(new ByteArrayInputStream(packet.getData)).readObject()
  }
}