package messanger.client

import java.net.{DatagramSocket, InetAddress, MulticastSocket, Socket}
import java.nio.channels.MulticastChannel

import messanger.tcp.ClientReadTCP
import messanger.udp.ClientReadUDP
import messanger.Server
import messanger.multicast.ClientReadMulticast


class Client {

  def run(): Unit = {
    var s: Socket = null
    var ds: DatagramSocket = null
    var ms: MulticastSocket = null
    try {
      val ip = InetAddress.getByName("localhost");
      s = new Socket(ip, Server.port);
      ds = new DatagramSocket
      ms = new MulticastSocket(6789)
      ms.joinGroup(Server.multicastAddress)

      val cs = new ClientSend(s.getOutputStream, ds, ms)
      val cr = new Thread(new ClientReadTCP(s.getInputStream))
      val crd = new Thread(new ClientReadUDP(ds))
      val cmd = new Thread(new ClientReadMulticast(ms))

      cs.start; cr.start; crd.start; cmd.start
      cs.join; cr.join; crd.join; cmd.join
    } catch {
      case x: java.net.ConnectException =>
        println("Connection to server is closed")
    } finally {
      if(s != null) s.close
      if(ds != null) ds.close
      if(ms != null) ms.close
    }
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    new Client().run
  }
}