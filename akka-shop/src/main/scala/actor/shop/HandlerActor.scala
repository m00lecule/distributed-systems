package actor.shop

import actor.logger.TLogger
import akka.actor.{Actor, ActorRef, Props}
import message.ServerResponse
import scala.language.postfixOps

class HandlerActor(val server: ActorRef, val shopId: Int, val Id: Int) extends Actor with TLogger {

  override val prefix = s"ShopHandler $Id at $shopId";

  val random = new scala.util.Random
  val start = 100
  val end = 500
  val delay = start + random.nextInt((end - start) + 1)
  val price = random.nextInt(end + 1)

  log(s"Initialized with delay $delay and price $price")

  def receive = {
    case id: Int => {
      log(s"Received query ID: $id")
      Thread.sleep(delay)
      server ! ServerResponse(id = id, price = price, name = "")
      log(s"Responded to client query ID: $id with price $price")
    }
  }
}

object HandlerActor {
  def apply(server: ActorRef, shopId: Int, Id: Int): Props = Props(new HandlerActor(server, shopId, Id))
}