package actor.router

import actor.logger.TLogger
import akka.actor.{Actor, ActorRef}
import message.ServerRequest

trait TRouterActor extends Actor with TLogger {

  val workers: List[ActorRef];

  def receive = {
    case sr: ServerRequest => {
      val index = forwardToHandler(sr)
      workers(index) ! sr
      log(s"Received query for ${sr.name}, forwarded it to worker $index")
    }
  }

  def forwardToHandler(request: ServerRequest): Int
}
