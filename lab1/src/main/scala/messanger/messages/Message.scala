package messanger.messages

case class Message(override val sender: String, override val message: String) extends MessageRef
