package actor.database


import java.sql.DriverManager

import actor.database.SQLiteJDBC.{c, stmt}
import message.{ClientResponse, ServerCountResponse, ServerRequest}
import akka.actor.{Actor, ActorRef}

class DatabaseActor extends Actor {

  initDatabase

  val workers: List[ActorRef] = List.tabulate(DatabaseActor.pool)(n => context.actorOf(DatabaseHandlerActor(context.parent, n), s"$n"));

  def initDatabase = {
    try {
      Class.forName(DatabaseActor.driver)
      val c = DriverManager.getConnection(DatabaseActor.connectionPath)
      val stmt = c.createStatement
      val sql = "CREATE TABLE IF NOT EXISTS QUERIES (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, NAME TEXT NOT NULL, " + " COUNT INT DEFAULT 1);"
      stmt.executeUpdate(sql)
      stmt.close()
      c.close()
    } catch {
      case e: Exception =>
        System.err.println(e.getClass.getName + ": " + e.getMessage)
        System.exit(0)
    }
  }

  def receive = {
    case sr@ServerRequest(_, name) =>
      val workerId = name.hashCode.abs % DatabaseActor.pool
      workers(workerId) ! sr
      log(s"Received query for $name, forwarded it to worker ${workerId}")
  }

  private def log(str: String) {
    context.system.log.info(s"[DatabaseActor] $str")
  }
}

object DatabaseActor {
  val driver = "org.sqlite.JDBC";
  val connectionPath = "jdbc:sqlite:test.db";
  val pool = 10;
}