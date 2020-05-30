import actor.client.ClientActor
import actor.server.ServerActor2
import message.Query
import akka.actor.ActorSystem

object Main2 extends App {
  val system = ActorSystem("SR_system")

  val server = system.actorOf(ServerActor2(2), name = "helloactor")
  val c = system.actorOf(ClientActor(server));
  val c2 = system.actorOf(ClientActor(server));
  val c3 = system.actorOf(ClientActor(server));

  c ! Query("jeb z dzidzy");
  c2 ! Query("jeb z dzidzy");
  c3 ! Query("jeb z dzidzy");
}