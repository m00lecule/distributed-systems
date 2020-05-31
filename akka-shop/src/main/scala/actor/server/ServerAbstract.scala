package actor.server

import akka.actor.{Actor, ActorRef}
import message.{ClientRequest, ServerResponse, ServerTimeout}

abstract class ServerAbstract extends Actor{


  override def receive = {

    case ClientRequest(name) => processClientRequest(name, sender())

    case ServerTimeout(id) => respondToClientRequest(id)

    case ServerResponse(id, name, price) => processHandlerResponse(id, name, price)

  }

  def processClientRequest(str: String, client: ActorRef)

  def respondToClientRequest(i: Int)

  def processHandlerResponse(i: Int, str: String, fl: Float)
}
