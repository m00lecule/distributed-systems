package actor.shop

import actor.router.TRouterActor
import message.ServerRequest
import akka.actor.{Actor, ActorRef, Props}

class ShopRouterActor(val count: Int, val Id: Int) extends TRouterActor {

  override val prefix: String = s"ShopRouterActor $Id"
  val server = context.parent;
  override val workers: List[ActorRef] = List.tabulate(count)(n => context.actorOf(ShopHandlerActor(server, Id, n), s"$n"));

  override def forwardToHandler(request: ServerRequest): Int = request.id % count
}

object ShopRouterActor {
  private var ID = 0;

  def getID() = ShopHandlerActor.synchronized {
    ID += 1
    ID
  }

  def apply(count: Int, Id: Int): Props = Props(new ShopRouterActor(count, Id))
}
