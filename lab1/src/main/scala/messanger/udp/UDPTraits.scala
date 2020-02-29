package messanger.udp

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.net.{DatagramPacket, DatagramSocket}

trait DatagramRead {

  val receive = new Array[Byte](65535)
  val packet = new DatagramPacket(receive, receive.length);
  val socket: DatagramSocket

  def readObject = {
    socket.receive(packet)
    new ObjectInputStream(new ByteArrayInputStream(packet.getData)).readObject()
  }
}