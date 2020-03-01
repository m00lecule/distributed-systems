package messanger.socket

import java.net.{DatagramSocket, InetAddress}

import messanger.Processable
import messanger.messages.MessageRef


class ClientUDPHandler(override val socket: DatagramSocket) extends DatagramObjectOperations[DatagramSocket] with Processable[(Int, InetAddress)] {
  var registeredClients: scala.collection.mutable.Set[(Int, InetAddress)] = scala.collection.mutable.Set()

  override def processLogout(sender: (Int, InetAddress)): Unit = registeredClients -= sender

  override def processMessage(message: MessageRef, sender: (Int, InetAddress)): Unit = {
    registeredClients += sender
    registeredClients.filterNot(_ equals sender).foreach(sendObjectTo(message, _))
  }
}
