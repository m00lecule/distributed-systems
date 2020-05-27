import Actors.Client.ClientActor
import Actors.Server.{ServerActor, ServerActor2}
import Messages.Query
import akka.actor.ActorSystem

object Main extends App {
  val system = ActorSystem("SR_system")

  val server = system.actorOf(ServerActor2(2), name = "helloactor")
  val c = system.actorOf(ClientActor(server));
  val c2 = system.actorOf(ClientActor(server));
  val c3 = system.actorOf(ClientActor(server));

  c ! Query("jeb z dzidzy");
  c2 ! Query("jeb z dzidzy");
  c3 ! Query("jeb z dzidzy");

}