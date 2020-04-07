package workers

import java.util.concurrent.atomic.AtomicInteger

import com.rabbitmq.client._
import workers.settings.{Exchange, Message, Settings}


class Carrier(override val connection: Connection, val tasks: List[String], val exchange: Exchange, override val routes: Map[Exchange, List[String]]) extends Mailbox {

  val processedOrders = new AtomicInteger(0)

  val channels: Map[String, Channel] = tasks.foldLeft(Map[String, Channel]()) { (m, t) => m + (t -> connection.createChannel()) }

  channels foreach { case (t, ch) => createListener(t, ch) }

  def createListener(queueName: String, channel: Channel): Unit = {
    channel.queueDeclare(queueName, true, false, false, null)
    channel.basicQos(1)
    channel.exchangeDeclare(exchange.name, exchange.route)
    channel.queueBind(queueName, exchange.name, queueName)

    val deliverCallback: DeliverCallback = (consumerTag: String, delivery: Delivery) => {

      val message: Message = deserialise[Message](delivery.getBody)

      printMes(queueName, message)

      Thread.sleep(4000)

      channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)

      val reply = Message(sender = queueName, topic = message.topic, id = processedOrders.getAndIncrement, message = "DONE")
      channel.basicPublish(exchange.name, message.sender, MessageProperties.PERSISTENT_TEXT_PLAIN, serialise(reply))
    }

    channel.basicConsume(queueName, false, deliverCallback, (_: String) => {})
  }
}


object Carrier {

  val carrierPrefix = "carrier";

  @throws[Exception]
  def main(argv: Array[String]): Unit = {
    val factory = new ConnectionFactory
    factory.setHost(Settings.IP)
    val connection = factory.newConnection

    val _: Carrier = new Carrier(connection, Settings.tasks, Settings.productionLine, Map(Settings.communicationLine -> List(carrierPrefix)))

  }
}
