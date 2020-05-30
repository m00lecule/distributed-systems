package actor.client

import message.{ClientRequest, ClientResponse, Query}
import akka.actor.{Actor, ActorRef, Props}


class ClientActor(val server: ActorRef) extends Actor {

  val Id = ClientActor.getID()

  def queryShop(name: String, server: ActorRef): Unit = {
    server ! ClientRequest(name)
  }

  def receive = {
    case ClientResponse(name, price, counter) =>
      log(s"Received response $name price: $price count: $counter")

    case Query(str) => server ! ClientRequest(str);
  }

  private def log(str: String) {
    context.system.log.info(s"[Client $Id] $str")
  }
}

object ClientActor {
  var ID = 0;

  def getID() = ClientActor.synchronized {
    ID += 1
    ID
  }

  def apply(server: ActorRef): Props = Props(new ClientActor(server))
}

