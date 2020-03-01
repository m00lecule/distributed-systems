package messanger

import messanger.messages.Message

trait Displayable extends Runnable {

  def readObject(): AnyRef

  def display(nick: String, message: String) = println("[" + nick + "] \n" + message)

  def close: Unit

  final def displayObject = {
    val received = readObject
    received match {
      case Message(nick, message) =>
        display(nick, message)
    }
  }

  override def run: Unit = {
    try {
      while (true)
        displayObject
    } catch {
      case e: Exception => e.printStackTrace; close
    }
  }
}
