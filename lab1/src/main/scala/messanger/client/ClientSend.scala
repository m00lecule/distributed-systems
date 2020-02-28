package messanger.client

import java.io.{ByteArrayOutputStream, ObjectOutputStream, OutputStream}
import java.net.{DatagramPacket, DatagramSocket}
import java.util.Scanner

import messanger.Server
import messanger.messages.Message

class ClientSend(val outputSocketStream: OutputStream, val socket: DatagramSocket) extends Thread {
  val output = new ObjectOutputStream(outputSocketStream)
  var nickname: String = null

  processInput("U hello packet")

  private def processInput(input: String): Unit = {

    input match {
      case s"U $rest" =>
        val byteOut = new ByteArrayOutputStream
        new ObjectOutputStream(byteOut).writeObject(Message(nickname, rest))
        socket.send(new DatagramPacket(byteOut.toByteArray, byteOut.toByteArray.length, Server.address, Server.port))
      case s"M $rest" =>
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
