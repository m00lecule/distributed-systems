package messanger.stream

import java.net.Socket

import messanger.messages.MessageRef
import messanger.{Processable, Server}

class ClientTCPHandler(override val socket: Socket, val server: Server) extends StreamObjectOperations with Processable[Socket] {

  override def processMessage(message: MessageRef, sender: Socket): Unit = {
    server.synchronized {
      val it = server.clientsTCP.iterator
      while(it.hasNext){
        val client = it.next
        if(client != this){
          try{
            client.writeObject(message)
          }catch {
            case _: Throwable =>
              client.isRunning = false
              server.clientsTCP.remove(client)
              println(server.clientsTCP)
          }
        }
      }
    }
  }

  override def processLogout(sender: Socket) = {
    isRunning = false
    server.clientsTCP.remove(this)
    println(server.clientsTCP)
  }

  override def processMessages: Unit = {
    try{
      super.processMessages
    }catch {
      case _: Throwable =>
        isRunning = false
        server.clientsTCP.remove(this)
        println(server.clientsTCP)
    }
  }
}

