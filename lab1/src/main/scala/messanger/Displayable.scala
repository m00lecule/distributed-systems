package messanger

import messanger.messages.Message

trait Displayable extends Runnable {

  def readObject(): AnyRef

  final def displayObject = {
    val received = readObject
    received match {
      case Message(nick, message) =>
        println("from " + nick + ": " + message)
    }
  }

  override def run {
    while (true)
      displayObject
  }
}
