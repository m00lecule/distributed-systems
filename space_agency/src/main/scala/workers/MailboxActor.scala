package workers

import com.rabbitmq.client.{Connection, DeliverCallback, Delivery}
import workers.representation.{Exchange, Prefix}

class MailboxActor(val connection: Connection, val routings: Map[Exchange, List[Any]]) {

  private val channel = connection.createChannel
  val queueName = channel.queueDeclare.getQueue

  routings foreach { case (exchange: Exchange, v: List[Any]) => {
    channel.exchangeDeclare(exchange.name, exchange.route)
    v.map {
      case Prefix(prefix) => channel.queueBind(queueName, exchange.name, queueName.replaceFirst("amq", prefix))
      case routingKey: String => channel.queueBind(queueName, exchange.name, routingKey)
    }
  }
  }

  val deliverCallback: DeliverCallback = (consumerTag: String, delivery: Delivery) => {
    val mes = new String(delivery.getBody, "UTF-8")
    println(" [x] Received '" + mes + "'")
  }
  channel.basicConsume(queueName, true, deliverCallback, (_: String) => {})
}
