package messanger.client

import java.io.{ByteArrayOutputStream, ObjectOutputStream, OutputStream}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, MulticastSocket}
import java.util.Scanner

import messanger.Server
import messanger.messages.Message

class ClientSend(val outputSocketStream: OutputStream, val socket: DatagramSocket, val multicastSocket: MulticastSocket) extends Thread {
  val output = new ObjectOutputStream(outputSocketStream)
  var nickname: String = null

  processInput("U hello packet")

  private def sendUsingSocket(message: Any, socket: DatagramSocket, ip: InetAddress, port: Int): Unit = {
    val byteOut = new ByteArrayOutputStream
    new ObjectOutputStream(byteOut).writeObject(message)
    socket.send(new DatagramPacket(byteOut.toByteArray, byteOut.toByteArray.length, ip, port))
  }

  private def processInput(input: String): Unit = {
    input match {
      case s"U $rest" =>
        sendUsingSocket(Message(nickname, rest), socket, Server.address, Server.port)
      case s"M $rest" =>
        sendUsingSocket(Message(nickname, rest), multicastSocket, Server.multicastAddress, 6789)
      case mess =>
        output.writeObject(Message(nickname, mess))
    }
  }

  override def run {
    val scn = new Scanner(System.in)
    println("Insert nickname:")
    nickname = scn.nextLine
    while (true)
      processInput(scn.nextLine)
  }
}
