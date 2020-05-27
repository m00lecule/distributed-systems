package Actors.Shop

import Messages.ServerResponse
import akka.actor.{Actor, ActorRef, Props}
import scala.language.postfixOps
import scala.concurrent.duration._

class HandlerActor(val server: ActorRef) extends Actor {
  import context.dispatcher

  val random = new scala.util.Random
  val start = 100
  val end = 500
  val delay = start + random.nextInt((end - start) + 1)
  val price = random.nextInt(end + 1)

  def receive = {
    case id: Int => {
      print(s"Revieved $id")

      context.system.scheduler.scheduleOnce(delay milliseconds, server, ServerResponse(id = id, price = price, name = ""))
    }
  }
}

object HandlerActor {
  def apply(server: ActorRef): Props = Props(new HandlerActor(server))
}