package messanger.socket

import java.net.MulticastSocket

import messanger.{Displayable, Server}

class MulticastSocketRead(private val nickname: String) extends DatagramObjectOperations[MulticastSocket] with Displayable {
  override val socket = new MulticastSocket(Server.multicastPort)
  socket.joinGroup(Server.multicastAddress)

  override def display(nick: String, message: String) = if (!nick.equals(nickname)) println("from " + nick + ": " + message)
}
