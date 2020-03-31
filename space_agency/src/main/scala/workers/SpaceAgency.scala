package workers

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory, DeliverCallback, Delivery, MessageProperties}
import workers.representation.{Exchange, Prefix, Settings}

class SpaceAgency(override val connection: Connection, val exchange: String, val routes: Map[Exchange, List[Any]]) extends MailboxActor(connection = connection, routings = routes) {

  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange, "topic")

  def publishOffer(QUEUE_NAME: String): Unit = {
    channel.basicPublish(Settings.productionLine.name, QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, queueName.replaceFirst("amq", "agency").getBytes("UTF-8"))
  }
}

object SpaceAgency {
  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost("localhost")
    val connection = factory.newConnection

    val sa: SpaceAgency = new SpaceAgency(connection, Settings.productionLine.name, Map(Settings.productionLine -> List(Prefix("agency"))))

    for (i <- 0 to 20) {
      sa.publishOffer(Settings.tasks.head)
    }

    for (i <- 0 to 20) {
      sa.publishOffer(Settings.tasks(1))
    }
  }
}