package messanger

import java.io.{ObjectOutputStream, OutputStream}
import java.util.Scanner

import messanger.messages.Message

class ClientSend(val outputSocketStream: OutputStream) extends Thread {
  val output = new ObjectOutputStream(outputSocketStream)

  override def run {
    val scn = new Scanner(System.in)
    println("Insert nickname:")
    var nickname = scn.nextLine
    while (true) {
      var input = scn.nextLine
      output.writeObject(Message(nickname, input))
    }
  }
}
