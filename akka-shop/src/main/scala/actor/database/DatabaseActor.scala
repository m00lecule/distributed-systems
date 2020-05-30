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
    case sr @ ServerRequest(_, name)  =>
      val workerId = name.hashCode.abs % DatabaseActor.pool
      workers(workerId) ! sr
      log(s"Received query for $name, forwarded it to worker ${workerId}")
  }

  private def log(str: String ){
    context.system.log.info(s"[DatabaseActor] $str")
  }
}


object DatabaseActor {
  val driver = "org.sqlite.JDBC";
  val connectionPath = "jdbc:sqlite:test.db";
  val pool = 10;
}


import java.sql._


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object SQLiteJDBC extends App {
  var c: Connection = null
  var stmt: Statement = null
  try {
    Class.forName("org.sqlite.JDBC")
    c = DriverManager.getConnection("jdbc:sqlite:test.db")
    stmt = c.createStatement
    val sql = "CREATE TABLE IF NOT EXISTS QUERIES " + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + " NAME TEXT NOT NULL, " + " COUNT INT DEFAULT 1);"
    stmt.executeUpdate(sql)
    stmt.close()
    c.close()
  } catch {
    case e: Exception =>
      System.err.println(e.getClass.getName + ": " + e.getMessage)
      System.exit(0)
  }
  System.out.println("Opened database successfully")

  import java.sql.DriverManager

  try {
    Class.forName("org.sqlite.JDBC")
    c = DriverManager.getConnection("jdbc:sqlite:test.db")
    System.out.println("Opened database successfully")
    stmt = c.createStatement
    var sql = "INSERT INTO QUERIES (NAME) " + "VALUES ('Paul');"
    stmt.executeUpdate(sql)
    stmt.close
    c.close
  } catch {
    case e: Exception =>
      System.err.println(e.getClass.getName + ": " + e.getMessage)
      System.exit(0)
  }
  System.out.println("Records created successfully")

  import java.sql.DriverManager
  import java.sql.ResultSet

  try {
    Class.forName("org.sqlite.JDBC")
    c = DriverManager.getConnection("jdbc:sqlite:test.db")
    c.setAutoCommit(false)
    System.out.println("Opened database successfully")
    stmt = c.createStatement
    val rs = stmt.executeQuery("SELECT * FROM QUERIES;")
    while ( {
      rs.next
    }) {
      val id = rs.getInt("id")
      val name = rs.getString("name")
      val salary = rs.getFloat("count")
      System.out.println("ID = " + id)
      System.out.println("NAME = " + name)
      System.out.println("SALARY = " + salary)
      System.out.println()
    }
    rs.close()
    stmt.close
    c.close
  } catch {
    case e: Exception =>
      System.err.println(e.getClass.getName + ": " + e.getMessage)
      System.exit(0)
  }
  System.out.println("Operation done successfully")


}