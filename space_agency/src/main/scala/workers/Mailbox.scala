package workers

import com.rabbitmq.client.{Connection, DeliverCallback, Delivery}
import workers.settings.{Exchange, Prefix, Settings}

trait Mailbox {

  val connection: Connection
  val routes: Map[Exchange, List[Any]]

  private val channel = connection.createChannel
  val queueName = channel.queueDeclare.getQueue

  routes foreach { case (exchange: Exchange, route: List[Any]) => {
    channel.exchangeDeclare(exchange.name, exchange.route)
    route.map {
      case Prefix(value) => channel.queueBind(queueName, exchange.name, queueName.replaceFirst(SpaceAgency.imqPrefix, value))
      case value: String => channel.queueBind(queueName, exchange.name, value)
    }
  }
  }

  val deliverCallback: DeliverCallback = (consumerTag: String, delivery: Delivery) => {
    val mes = new String(delivery.getBody, Settings.encoding)
    println(" [x] Received '" + mes + "'")
  }
  channel.basicConsume(queueName, true, deliverCallback, (_: String) => {})
}
