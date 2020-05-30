package actor.server

import java.util
import actor.database.DatabaseActor
import actor.shop.ShopActor
import message.{ClientRequest, ClientResponse, ServerCountResponse, ServerRequest, ServerResponse, ServerTimeout}
import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

class ServerActor2(val shopsCount: Int) extends Actor {

  import context.dispatcher

  val threads = 10
  val shops: List[ActorRef] = List.tabulate(shopsCount)(n => context.actorOf(ShopActor(threads, n)));

  val database: ActorRef = context.actorOf(Props(new DatabaseActor));
  var id = 0
  val requests = new util.HashMap[Int, (Option[Float], String, ActorRef)]();
  val countMap = new util.HashMap[Int, Int]();

  def receive = {

    case ServerCountResponse(id, count) =>
      countMap.put(id, count);

    case ClientRequest(name) => {
      requests.put(id, (None, name, sender()))
      val scheduledRequest = ServerRequest(id, name)
      shops.foreach(s => s ! scheduledRequest)
      database ! scheduledRequest
      context.system.scheduler.scheduleOnce(300 milliseconds, self, ServerTimeout(id))
      id += 1
    }

    case ServerTimeout(id) => {
      val (value, name, sender) = requests.get(id);

      var counter: Option[Int] = None;
      if (countMap.containsKey(id)) {
        counter = Some(countMap.get(id))
      }

      sender ! ClientResponse(name, value, counter)
      requests.remove(id)
    }

    case ServerResponse(id, name, price) =>
      if (requests.containsKey(id)) {
        val (r_value, r_name, r_sender) = requests.get(id);
        r_value match {
          case Some(p) if p.compareTo(price) > 0 => requests.put(id, (Some(price), r_name, r_sender))
          case _ => requests.put(id, (Some(price), r_name, r_sender))
        }
      }
  }
}

object ServerActor2 {
  def apply(count: Int): Props = Props(new ServerActor2(count))
}