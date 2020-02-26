package messanger

import java.io.{EOFException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import messanger.messages.{LogoutMessage, Message}

class ClientHandler(val socket: Socket, val server: Server) extends Runnable with IProcessMessages {

  var isRunning = true;
  var input: ObjectInputStream = null
  var output: ObjectOutputStream = null

  def processMessages(): Unit = {
    val received = input.readObject();
    received match {
      case mess: Message =>
        server.clientsTCP.stream().filter(c => !c.equals(this)).forEach(c => c.output.writeObject(mess))
      case mess: LogoutMessage =>
        server.clientsTCP.remove(this)
        isRunning = false
    }
  }

  override def run(): Unit = {
    try {
      input = new ObjectInputStream(socket.getInputStream)
      output = new ObjectOutputStream(socket.getOutputStream)

      while (isRunning) {
        processMessages
      }
    } catch {
      case x: EOFException =>
      case x: java.net.SocketException =>
    } finally {
      if (input != null) input.close
      if (output != null) output.close
    }
  }
}