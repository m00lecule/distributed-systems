package workers

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory, DeliverCallback, Delivery, MessageProperties}
import workers.settings.{Exchange, Prefix, Settings}

class SpaceAgency(override val connection: Connection, val exchange: String, override val routes: Map[Exchange, List[Any]]) extends Mailbox {

  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange, "topic")

  def publishOffer(QUEUE_NAME: String): Unit = {
    channel.basicPublish(Settings.productionLine.name, QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, queueName.replaceFirst(SpaceAgency.imqPrefix, SpaceAgency.agencyPrefix).getBytes(Settings.encoding))
  }
}

object SpaceAgency {

  val agencyPrefix: String = "agency"
  val imqPrefix: String = "amq"

  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost(Settings.IP)
    val connection = factory.newConnection

    val sa: SpaceAgency = new SpaceAgency(connection, Settings.productionLine.name, Map(Settings.productionLine -> List(Prefix(agencyPrefix))))

    for (i <- 0 to 20) {
      sa.publishOffer(Settings.tasks.head)
    }

    for (i <- 0 to 20) {
      sa.publishOffer(Settings.tasks(1))
    }
  }
}