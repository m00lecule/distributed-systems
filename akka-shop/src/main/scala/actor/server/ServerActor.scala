package actor.server

import java.util

import actor.shop.ShopRouterActor
import message._
import akka.actor.{ActorRef, Props}
import scala.language.postfixOps

import scala.concurrent.duration._

class ServerActor(val shopsCount: Int) extends ServerAbstract {

  import context.dispatcher

  val threads = 10
  val shops: List[ActorRef] = List.tabulate(shopsCount)(n => context.actorOf(ShopRouterActor(threads, n)));
  var id = 0
  val requests = new util.HashMap[Int, (Option[Float], String, ActorRef)]();

  override def processClientRequest(name: String, client: ActorRef) = {
    requests.put(id, (None, name, client))
    val scheduledRequest = ServerRequest(id, name)
    shops.foreach(s => s ! scheduledRequest)
    context.system.scheduler.scheduleOnce(300 milliseconds, self, ServerTimeout(id))
    id += 1
  }

  override def respondToClientRequest(id: Int) = {
    if (requests.containsKey(id)) {
      val (value, name, sender) = requests.get(id);
      sender ! ClientResponse(name, value, counter = None)
      requests.remove(id)
    }
  }

  override def processHandlerResponse(id: Int, name: String, price: Float) = {
    if (requests.containsKey(id)) {
      val (r_value, r_name, r_sender) = requests.get(id);
      r_value match {
        case Some(p) if p.compareTo(price) > 0 =>
          sender ! ClientResponse(name, Some(price), counter = None)
          requests.remove(id)
        case value@Some(_) =>
          sender ! ClientResponse(name, value, counter = None)
          requests.remove(id)
        case _ => requests.put(id, (Some(price), r_name, r_sender))
      }
    }
  }
}

object ServerActor {
  def apply(count: Int): Props = Props(new ServerActor(count))
}