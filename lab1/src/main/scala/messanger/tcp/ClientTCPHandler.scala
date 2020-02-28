package messanger.tcp

import java.io.{EOFException, InputStream, ObjectOutputStream}
import java.net.Socket

import messanger.messages.Message
import messanger.{Processable, Server}

class ClientTCPHandler(val socket: Socket, val server: Server, override val inputSocketStream: InputStream) extends Runnable with Processable with TCPRead {

  var output: ObjectOutputStream = null

  override def processMessage(message: Message): Unit = server.clientsTCP.stream().filter(!_.equals(this)).forEach(_.output.writeObject(message))

  override def processLogout = server.clientsTCP.remove(this)

  override def run(): Unit = {
    try {
      output = new ObjectOutputStream(socket.getOutputStream)

      while (isRunning)
        processMessages

    } catch {
      case x: EOFException =>
      case x: java.net.SocketException =>
    } finally {
      if (input != null) input.close
      if (output != null) output.close
      if (socket != null) socket.close
    }
  }
}