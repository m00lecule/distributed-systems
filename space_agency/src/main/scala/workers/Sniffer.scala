package workers

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory, DeliverCallback, Delivery, MessageProperties}
import workers.settings.{Exchange, Settings}

class Sniffer(override val connection: Connection, val exchange: Exchange, override val routes: Map[Exchange, List[Any]]) extends Mailbox {
  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange.name, exchange.route)
}

object Sniffer {
  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost(Settings.IP)
    val connection = factory.newConnection

    val _: Sniffer = new Sniffer(connection, Settings.communicationLine, Map(Settings.productionLine -> List(("#.*"))))

  }
}