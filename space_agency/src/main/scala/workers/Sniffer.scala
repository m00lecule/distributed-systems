package workers

import java.util.concurrent.atomic.AtomicInteger

import com.rabbitmq.client.{Connection, ConnectionFactory, MessageProperties}
import workers.settings.{Exchange, Message, Settings}

class Sniffer(override val connection: Connection, val exchange: Exchange, override val routes: Map[Exchange, List[Any]]) extends Mailbox {

  val spamId = new AtomicInteger(0)

  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange.name, exchange.route)

  def broadcast(msg: String) = {
    agencyBroadcast(msg)
    carriedBroadcast(msg)
  }

  def agencyBroadcast(msg: String) = publishOffer(SpaceAgency.agencyPrefix, message = Message(sender = "Sniffer", topic = "SPAM TO " + SpaceAgency.agencyPrefix, message = msg, id = spamId.getAndIncrement))

  def carriedBroadcast(msg: String) = publishOffer(Carrier.carrierPrefix, message = Message(sender = "Sniffer", topic = "SPAM TO " + SpaceAgency.agencyPrefix, message = msg, id = spamId.getAndIncrement))

  private def publishOffer(QUEUE_NAME: String, message: Message): Unit = {
    channel.basicPublish(Settings.communicationLine.name, QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, serialize(message))
  }
}

object Sniffer {
  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost(Settings.IP)
    val connection = factory.newConnection

    val s: Sniffer = new Sniffer(connection, Settings.communicationLine, Map(Settings.productionLine -> List(("#.*"))))

    var cond = true

    while (cond) {
      val input = scala.io.StdIn.readLine(">> INSERT MESSAGE:")

      if (input.eq("BREAK"))
        cond = false

      println(">> insert A - [AGENCY] C - [CARRIER] B - [BOTH")

      val index = scala.io.StdIn.readLine()

      index match {
        case "A" => s.agencyBroadcast(input)
        case "C" => s.carriedBroadcast(input)
        case "B" => s.broadcast(input)
        case "BREAK" => cond = false
        case _ => println("Wrong character")
      }
    }

  }
}