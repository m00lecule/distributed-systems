package actor.search

import java.util

import actor.logger.TLogger
import akka.actor.{Actor, ActorRef, Props}
import message.{ClientRequest, ClientSearchResponse, ServerRequest, ServerSearchResponse}

class SearchLoadBalancerActor extends Actor with TLogger {

  override val prefix = "SearchLoadBalancer"
  val count = SearchLoadBalancerActor.workers
  val workers: List[ActorRef] = List.tabulate(count)(n => context.actorOf(Props(new SearchHandlerActor(n)), s"$n"));
  val requests = new util.HashMap[Int, (String, ActorRef)]()
  var id = 0;



  def receive = {
    case ClientRequest(name) => {
      id += 1
      requests.put(id, (name, sender()))
      val index = id % count
      workers(index) ! ServerRequest(id, name)
      log(s"Assigned search for $name with $id and forwarder to handler $index")
    }
    case ServerSearchResponse(id, pros) =>
      val (name, sender) = requests.get(id);
      sender ! ClientSearchResponse(name, pros)
      log(s"Responded for $name search with $pros")
  }
}

object SearchLoadBalancerActor {
  val workers = 10;
}
