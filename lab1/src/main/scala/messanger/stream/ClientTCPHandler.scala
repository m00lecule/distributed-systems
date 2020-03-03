package messanger.stream

import java.net.Socket

import messanger.messages.MessageRef
import messanger.{Processable, Server}

class ClientTCPHandler(override val socket: Socket, val server: Server) extends StreamObjectOperations with Processable[Socket] {

  override def processMessage(message: MessageRef, sender: Socket): Unit = server.clientsTCP.stream().filter(!_.equals(this)).forEach(_.writeObject(message))

  override def processLogout(sender: Socket) = server.clientsTCP.remove(this)
}

