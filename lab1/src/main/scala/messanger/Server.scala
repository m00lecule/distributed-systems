package messanger

import java.net.{ServerSocket, Socket}
import java.util.concurrent.{CopyOnWriteArrayList, ExecutorService, Executors}

class Server {
  val ss = new ServerSocket(Server.port)
  var clientsTCP: CopyOnWriteArrayList[ClientHandler] = new CopyOnWriteArrayList()
  val pool: ExecutorService = Executors.newFixedThreadPool(Server.poolSize)

  def run(): Unit = {
    var socket: Socket = null
    while (true) {
      socket = ss.accept()
      println("Connection accepted")
      val clientHandler = new ClientHandler(socket, this)
      clientsTCP.add(clientHandler)
      pool.execute(clientHandler)
    }
  }
}

object Server {
  val port = 1100
  val poolSize = 100

  def main(args: Array[String]): Unit = {
    new Server().run()
  }
}
