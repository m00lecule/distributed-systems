package workers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.rabbitmq.client.{Connection, DeliverCallback, Delivery}
import workers.settings.{Exchange, Message, Prefix}

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
    val mes = deserialise[Message](delivery.getBody)
    printMes("MAILBOX", mes)
  }
  channel.basicConsume(queueName, true, deliverCallback, (_: String) => {})

  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close()
    stream.toByteArray
  }

  def deserialise[A](bytes: Array[Byte]): A = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value: A = ois.readObject.asInstanceOf[A]
    ois.close()
    value
  }

  protected def printMes(topic: String, msg: Message) = println("[" + topic + "] " + msg.toString + "\t")

}
