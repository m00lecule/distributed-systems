package messanger

import java.net.{InetAddress, Socket}


class Client {
  val ip = InetAddress.getByName("localhost");
  val s = new Socket(ip, Server.port);

  def run(): Unit = {
    val cs = new ClientSend(s.getOutputStream)
    val cr = new ClientRead(s.getInputStream)

    cs.start; cr.start
    cs.join; cr.join
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    try {
      new Client().run
    } catch {
      case x: java.net.ConnectException =>
        println("Connection to server is closed")
    }
  }
}