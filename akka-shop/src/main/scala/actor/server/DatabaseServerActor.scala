package actor.server

import java.util
import actor.database.DatabaseActor
import message.{ClientResponse, ServerCountResponse, ServerRequest, ServerTimeout}
import akka.actor.{ActorRef, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

class DatabaseServerActor(override val shopsCount: Int) extends ServerActor(shopsCount) {

  import context.dispatcher

  val database: ActorRef = context.actorOf(Props(new DatabaseActor));
  val countMap = new util.HashMap[Int, Int]();

  override def receive = {
    super.receive orElse {
      case ServerCountResponse(id, count) =>
        countMap.put(id, count);
    }
  }

  override def processClientRequest(name: String, client: ActorRef) = {
    requests.put(id, (None, name, client))
    val scheduledRequest = ServerRequest(id, name)
    shops.foreach(s => s ! scheduledRequest)
    database ! scheduledRequest
    context.system.scheduler.scheduleOnce(300 milliseconds, self, ServerTimeout(id))
    id += 1
  }

  override def respondToClientRequest(id: Int) = {
    val (value, name, sender) = requests.get(id);

    var counter: Option[Int] = None;
    if (countMap.containsKey(id)) {
      counter = Some(countMap.get(id))
    }

    sender ! ClientResponse(name, value, counter)
    requests.remove(id)
  }

  override def processHandlerResponse(id: Int, name: String, price: Float) = {
    if (requests.containsKey(id)) {
      val (r_value, r_name, r_sender) = requests.get(id);
      r_value match {
        case Some(p) if p.compareTo(price) > 0 => requests.put(id, (Some(price), r_name, r_sender))
        case _ => requests.put(id, (Some(price), r_name, r_sender))
      }
    }
  }
}

object DatabaseServerActor {
  def apply(count: Int): Props = Props(new DatabaseServerActor(count))
}