package messanger

import java.net.{DatagramSocket, InetAddress, ServerSocket, Socket}
import java.util.concurrent.{CopyOnWriteArrayList, ExecutorService, Executors}

import messanger.tcp.ClientTCPHandler
import messanger.udp.ClientUDPHandler

class Server {
  val ss = new ServerSocket(Server.port)
  var clientsTCP: CopyOnWriteArrayList[ClientTCPHandler] = new CopyOnWriteArrayList()
  val pool: ExecutorService = Executors.newFixedThreadPool(Server.poolSize)

  def run(): Unit = {
    var socket: Socket = null
    pool.execute(new ClientUDPHandler(new DatagramSocket(Server.port)))
    while (true) {
      socket = ss.accept()
      println("Connection accepted")
      val clientHandler = new ClientTCPHandler(socket, this, socket.getInputStream)
      clientsTCP.add(clientHandler)
      pool.execute(clientHandler)
    }
  }
}

object Server {
  val port = 1100
  val poolSize = 100
  val address = InetAddress.getByName("localhost");
  val multicastAddress = InetAddress.getByName("224.168.1.124");

  def main(args: Array[String]): Unit = {
    new Server().run()
  }
}
