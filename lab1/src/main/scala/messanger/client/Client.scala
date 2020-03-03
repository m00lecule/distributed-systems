package messanger.client

import java.util.Scanner
import messanger.socket.{DatagramSocketRead, MulticastSocketRead}
import messanger.stream.TCPSocketRead

class Client {
  def run(): Unit = {
    try {
      val scn = new Scanner(System.in)
      println("Insert nickname:")
      val nickname = scn.nextLine
      val multicast = new MulticastSocketRead(nickname)
      val datagram = new DatagramSocketRead
      val tcp = new TCPSocketRead
      val send = new ClientSend(nickname, tcp.socket, datagram.socket, multicast.socket)
      val tasks: List[Thread] = List(new Thread(tcp), new Thread(datagram), new Thread(multicast), new Thread(send))
      tasks.foreach(_.start)
      tasks.foreach(_.join)
    } catch {
      case x: Exception => x.printStackTrace()
    }
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    new Client().run
  }
}