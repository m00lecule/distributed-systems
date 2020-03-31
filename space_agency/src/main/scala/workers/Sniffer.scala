package workers

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory, DeliverCallback, Delivery, MessageProperties}
import workers.representation.{Exchange, Settings}

class Sniffer(override val connection: Connection, val exchange: String, val routes: Map[Exchange, List[Any]]) extends MailboxActor(connection = connection, routings = routes) {
  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange, "topic")
}

object Sniffer {
  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost("localhost")
    val connection = factory.newConnection

    val _: Sniffer = new Sniffer(connection, Settings.productionLine.name, Map(Settings.productionLine -> List(("#.*"))))

  }
}