package actor.database

import java.sql.DriverManager

import actor.logger.TLogger
import message.{ServerCountResponse, ServerRequest}
import akka.actor.{Actor, ActorRef, Props}

class DatabaseHandlerActor(val server: ActorRef, val Id: Int) extends Actor with TLogger {

  override val prefix = s"DatabaseHandler $Id"

  log(s"Initialized")

  def create(name: String) = {
    try {
      Class.forName(DatabaseRouterActor.driver)
      val c = DriverManager.getConnection(DatabaseRouterActor.connectionPath)
      val stmt = c.createStatement
      val sql = s"INSERT INTO QUERIES (NAME) VALUES ('$name');"
      stmt.executeUpdate(sql)
      stmt.close
      c.close
    } catch {
      case e: Exception =>
        System.err.println(e.getClass.getName + ": " + e.getMessage)
        System.exit(0)
    }
  }

  def getCount(name: String): Option[Int] = {
    var count: Option[Int] = None;
    try {
      Class.forName(DatabaseRouterActor.driver)
      val c = DriverManager.getConnection(DatabaseRouterActor.connectionPath)
      c.setAutoCommit(false)
      val stmt = c.createStatement
      val rs = stmt.executeQuery(s"SELECT COUNT FROM QUERIES WHERE NAME IS '$name';")
      while (rs.next) {
        count = Some(rs.getInt("count"))
      }
      rs.close()
      stmt.close
      c.close
    } catch {
      case e: Exception =>
        System.err.println(e.getClass.getName + ": " + e.getMessage)
        System.exit(0)
    }
    count;
  }

  def increment(name: String, previous: Int): Unit = {
    try {
      Class.forName(DatabaseRouterActor.driver)
      val c = DriverManager.getConnection(DatabaseRouterActor.connectionPath)
      val stmt = c.createStatement
      stmt.executeUpdate(s"UPDATE QUERIES SET COUNT = $previous WHERE name = '$name';")
      stmt.close
      c.close
    } catch {
      case e: Exception =>
        System.err.println(e.getClass.getName + ": " + e.getMessage)
        System.exit(0)
    }
  }

  def receive = {
    case ServerRequest(id, name) =>

      log(s"Received query for $name Id: $id")

      val count = getCount(name)

      count match {
        case Some(count) => {
          server ! ServerCountResponse(id, count + 1)
          increment(name, count + 1)
          log(s"Responded for existing registry $name count: ${count + 1}")
        }
        case _ =>
          server ! ServerCountResponse(id = id, count = 1)
          log(s"Created registry for $name with count 1")
          create(name)
      }
  }
}

object DatabaseHandlerActor {
  def apply(server: ActorRef, Id: Int): Props = Props(new DatabaseHandlerActor(server, Id))
}
