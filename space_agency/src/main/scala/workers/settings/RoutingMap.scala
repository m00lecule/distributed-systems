package workers.settings

case class Prefix(prefix: String)

case class Exchange(name: String, route: String)

case class Message(sender: String, topic: String, message: String = "DO", id: Int) {

  override def toString: String = "ID: " + id + " [TOPIC] " + topic + " [SENDER] " + sender + " [MSG] " + message
}
