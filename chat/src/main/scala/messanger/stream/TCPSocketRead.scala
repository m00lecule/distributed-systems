package messanger.stream

import java.net.{InetAddress, Socket}

import messanger.{Displayable, Server}

class TCPSocketRead extends StreamObjectOperations with Displayable {
  val ip = InetAddress.getByName("localhost");
  override val socket = new Socket(ip, Server.port)
}
