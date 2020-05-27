package Actors.Database


import java.sql.DriverManager

import Actors.Database.SQLiteJDBC.{c, stmt}
import Messages.{ClientResponse, ServerCountResponse, ServerRequest}
import akka.actor.{Actor, ActorRef, Props}

class DatabaseHandlerActor(val server: ActorRef) extends Actor {
  def create(name: String) = {
    try {
      Class.forName(DatabaseActor.driver)
      val c = DriverManager.getConnection(DatabaseActor.connectionPath)
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
      Class.forName(DatabaseActor.driver)
      val c = DriverManager.getConnection(DatabaseActor.connectionPath)
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
      Class.forName(DatabaseActor.driver)
      val c = DriverManager.getConnection(DatabaseActor.connectionPath)
      stmt = c.createStatement
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
      val count = getCount(name)

      count match {
        case Some(count) => {
          server ! ServerCountResponse(id=id, count=count)
          increment(name, count + 1)
        }
        case _ =>
          server ! ServerCountResponse(id=id, count=1)
          create(name)
      }
  }

}

object DatabaseHandlerActor {
  def apply(server: ActorRef): Props = Props(new DatabaseHandlerActor(server))
}
