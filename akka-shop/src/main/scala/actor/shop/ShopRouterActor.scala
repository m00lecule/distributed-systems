package actor.shop

import message.ServerRequest
import akka.actor.{Actor, ActorRef, Props}

class ShopRouterActor(val count: Int, val Id: Int) extends Actor {

  val server = context.parent;
  val workers: List[ActorRef] = List.tabulate(count)(n => context.actorOf(HandlerActor(server, Id, n), s"$n"));

  def receive = {
    case ServerRequest(id, _) => {
      workers(id % count) ! id
    }
  }
}

object ShopRouterActor {
  private var ID = 0;

  def getID() = HandlerActor.synchronized {
    ID += 1
    ID
  }

  def apply(count: Int, Id: Int): Props = Props(new ShopRouterActor(count, Id))
}
