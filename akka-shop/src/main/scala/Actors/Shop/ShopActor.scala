package Actors.Shop

import Messages.ServerRequest
import akka.actor.{Actor, ActorRef, Props}

class ShopActor(val count: Int) extends Actor {

  val server = context.parent;
  val workers: List[ActorRef] = List.tabulate(count)(n => context.actorOf(HandlerActor(server), s"$n"));

  def receive = {
    case ServerRequest(id, _) => {
      workers(id % count) ! id
    }
  }
}

object ShopActor {
  def apply(count: Int): Props = Props(new ShopActor(count))
}
