package messanger.client

import java.net.{DatagramSocket, InetAddress, Socket}

import messanger.tcp.ClientReadTCP
import messanger.udp.ClientReadUDP
import messanger.Server


class Client {

  def run(): Unit = {
    var s: Socket = null
    var ds: DatagramSocket = null
    try {
      val ip = InetAddress.getByName("localhost");
      s = new Socket(ip, Server.port);
      ds = new DatagramSocket
      val cs = new ClientSend(s.getOutputStream, ds)
      val cr = new Thread(new ClientReadTCP(s.getInputStream))
      val crd = new Thread(new ClientReadUDP(ds))

      cs.start; cr.start; crd.start
      cs.join; cr.join; crd.join
    } catch {
      case x: java.net.ConnectException =>
        println("Connection to server is closed")
    } finally {
      s.close
    }
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    new Client().run
  }
}