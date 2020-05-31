package actor.server

import java.util
import actor.database.DatabaseRouterActor
import message.{ClientResponse, ServerCountResponse, ServerRequest, ServerTimeout}
import akka.actor.{ActorRef, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

class DatabaseServerActor(override val shopsCount: Int) extends ServerActor(shopsCount) {

  import context.dispatcher

  val database: ActorRef = context.actorOf(Props(new DatabaseRouterActor));
  val countMap = new util.HashMap[Int, Option[Int]]();

  override def receive = {
    super.receive orElse {
      case ServerCountResponse(id, count) =>
        countMap.put(id, Some(count));
    }
  }

  override def processClientRequest(name: String, client: ActorRef) = {
    database ! ServerRequest(id, name)
    super.processClientRequest(name, client)
  }

  override def respondToClientRequest(id: Int, counter: Option[Int] = None) = {
    super.respondToClientRequest(id, countMap.getOrDefault(id, None))
  }

  override def processHandlerResponse(id: Int, name: String, price: Float) = {
    if (requests.containsKey(id)) {
      val (r_value, r_name, r_sender) = requests.get(id);
      r_value match {
        case Some(p) if p.compareTo(price) > 0 => requests.put(id, (Some(price), r_name, r_sender))
        case None => requests.put(id, (Some(price), r_name, r_sender))
        case _ => ()
      }
    }
  }
}

object DatabaseServerActor {
  def apply(count: Int): Props = Props(new DatabaseServerActor(count))
}