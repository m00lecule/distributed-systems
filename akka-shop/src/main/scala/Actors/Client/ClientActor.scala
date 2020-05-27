package Actors.Client

import Messages.{ClientRequest, ClientResponse, Query}
import akka.actor.{Actor, ActorRef, Props}


class ClientActor(val server: ActorRef) extends Actor {

  def queryShop(name: String, server: ActorRef): Unit = {
    server ! ClientRequest(name)
  }

  def receive = {
    case ClientResponse(str, counter) =>
      println(str)

      counter match {
        case Some(count) => println(s"client count $count")
        case _ => ()
      }
    case Query(str) => server ! ClientRequest(str);
  }
}

object ClientActor {
  def apply(server: ActorRef): Props = Props(new ClientActor(server))
}

