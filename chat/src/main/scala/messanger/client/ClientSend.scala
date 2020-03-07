package messanger.client

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, MulticastSocket, Socket}
import java.util.Scanner

import art.ASCIIArtGenerator
import messanger.Server
import messanger.messages.{ASCIIArtMessage, LogoutMessage, Message, MessageRef}

class ClientSend(val nickname: String, val socket: Socket, val datagramSocket: DatagramSocket, val multicastSocket: MulticastSocket) extends Thread {
  processInput("U hello packet")

  val generator = new ASCIIArtGenerator()

  private def sendUsingSocket(message: AnyRef, socket: DatagramSocket, ip: InetAddress, port: Int): Unit = {
    val byteOut = new ByteArrayOutputStream
    new ObjectOutputStream(byteOut).writeObject(message)
    socket.send(new DatagramPacket(byteOut.toByteArray, byteOut.toByteArray.length, ip, port))
  }

  private def processInput(input: String): Unit = {
    input match {
      case s"U $rest" =>
        sendUsingSocket(Message(nickname, rest), datagramSocket, Server.address, Server.port)
      case s"M $rest" =>
        sendUsingSocket(Message(nickname, rest), multicastSocket, Server.multicastAddress, Server.multicastPort)
      case s"A $rest" =>
        val output = new ObjectOutputStream(socket.getOutputStream)
        output.writeObject(ASCIIArtMessage(nickname, rest))
      case mess =>
        val output = new ObjectOutputStream(socket.getOutputStream)
        output.writeObject(Message(nickname, mess))
    }
  }

  override def run {
    val scn = new Scanner(System.in)
    try {
      while (true)
        processInput(scn.nextLine)
    }catch {
      case x: Exception => logOut
    }
  }

  def logOut(): Unit = {
    sendUsingSocket(LogoutMessage(), datagramSocket, Server.address, Server.port)
    val output = new ObjectOutputStream(socket.getOutputStream)
    output.writeObject(LogoutMessage())
  }
}
