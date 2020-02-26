package messanger

import java.io.{DataInputStream, DataOutputStream, InputStream, ObjectInputStream, ObjectOutputStream}
import java.util.Scanner

import messanger.messages.Message

class ClientRead(val inputSocketStream: InputStream) extends Thread with IProcessMessages {
  val input = new ObjectInputStream(inputSocketStream)

  def processMessages {
    val received = input.readObject();
    received match {
      case Message(nick, message) =>
        println("from " + nick + ": " + message)
    }
  }

  override def run {
    while (true) {
      processMessages
    }
  }
}
