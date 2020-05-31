package actor.database


import java.sql.DriverManager

import actor.router.TRouterActor
import message.ServerRequest
import akka.actor.ActorRef

class DatabaseRouterActor extends TRouterActor {

  override val prefix = "DatabaseActor"

  initDatabase

  override val workers: List[ActorRef] = List.tabulate(DatabaseRouterActor.pool)(n => context.actorOf(DatabaseHandlerActor(context.parent, n), s"$n"));

  def initDatabase = {
    try {
      Class.forName(DatabaseRouterActor.driver)
      val c = DriverManager.getConnection(DatabaseRouterActor.connectionPath)
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

  override def forwardToHandler(request: ServerRequest): Int = request.name.hashCode.abs % DatabaseRouterActor.pool
}

object DatabaseRouterActor {
  val driver = "org.sqlite.JDBC";
  val connectionPath = "jdbc:sqlite:test.db";
  val pool = 10;
}