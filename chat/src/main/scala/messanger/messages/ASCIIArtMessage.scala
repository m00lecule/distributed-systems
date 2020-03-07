package messanger.messages

case class ASCIIArtMessage(override val sender: String, override val message: String) extends MessageRef
