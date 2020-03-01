package messanger.client

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, MulticastSocket, Socket}
import java.util.Scanner

import art.ASCIIArtGenerator
import messanger.Server
import messanger.messages.Message

class ClientSend(val nickname: String, val socket: Socket, val datagramSocket: DatagramSocket, val multicastSocket: MulticastSocket) extends Thread {
  processInput("U hello packet")

  val generator = new ASCIIArtGenerator()

  private def sendUsingSocket(message: Any, socket: DatagramSocket, ip: InetAddress, port: Int): Unit = {
    val byteOut = new ByteArrayOutputStream
    new ObjectOutputStream(byteOut).writeObject(message)
    socket.send(new DatagramPacket(byteOut.toByteArray, byteOut.toByteArray.length, ip, port))
    byteOut.close
  }

  private def processInput(input: String): Unit = {
    input match {
      case s"U $rest" =>
        sendUsingSocket(Message(nickname, rest), datagramSocket, Server.address, Server.port)
      case s"M $rest" =>
        sendUsingSocket(Message(nickname, rest), multicastSocket, Server.multicastAddress, 6789)
      case s"ASCII $rest" =>
        val output = new ObjectOutputStream(socket.getOutputStream)
        output.writeObject(Message(nickname, generator.printTextArt(rest, ASCIIArtGenerator.ART_SIZE_MEDIUM)))
      case mess =>
        val output = new ObjectOutputStream(socket.getOutputStream)
        output.writeObject(Message(nickname, mess))
    }
  }

  override def run {
    val scn = new Scanner(System.in)
    while (true)
      processInput(scn.nextLine)
  }
}
