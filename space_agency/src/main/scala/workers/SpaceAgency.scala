package workers

import java.util.concurrent.atomic.AtomicInteger

import com.rabbitmq.client.{Connection, ConnectionFactory, MessageProperties}
import workers.settings.{Exchange, Message, Prefix, Settings}

class SpaceAgency(override val connection: Connection, val exchange: String, override val routes: Map[Exchange, List[Any]]) extends Mailbox {

  val outsourceOrders = new AtomicInteger(0)

  private val channel = connection.createChannel()
  channel.exchangeDeclare(exchange, "topic")

  def publishOffer(QUEUE_NAME: String, msg: String): Unit = {
    val message = Message(topic = QUEUE_NAME, message = msg, sender = queueName.replaceFirst(SpaceAgency.imqPrefix, SpaceAgency.agencyPrefix), id = outsourceOrders.incrementAndGet)
    channel.basicPublish(Settings.productionLine.name, QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, serialise(message))
  }
}

object SpaceAgency {

  val agencyPrefix: String = "agency"
  val imqPrefix: String = "amq"

  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost(Settings.IP)
    val connection = factory.newConnection

    val sa: SpaceAgency = new SpaceAgency(connection, Settings.productionLine.name, Map(Settings.productionLine -> List(Prefix(agencyPrefix)), Settings.communicationLine -> List(agencyPrefix)))

    var cond = true

    while (cond) {
      val input = scala.io.StdIn.readLine(">> INSERT MESSAGE:")

      if (input.eq("BREAK"))
        cond = false

      println(">>: " + Settings.tasks)

      val index = scala.io.StdIn.readInt()

      sa.publishOffer(Settings.tasks(index), input)
    }
  }
}