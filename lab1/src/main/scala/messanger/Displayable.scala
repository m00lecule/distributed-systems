package messanger

import art.ASCIIArtGenerator
import messanger.messages.{ASCIIArtMessage, Message}

trait Displayable extends Runnable {

  def readObject(): AnyRef

  def display(nick: String, message: String) = println("[" + nick + "] \n" + message)

  def close: Unit

  val generator = new ASCIIArtGenerator

  final def displayObject = {
    val received = readObject
    received match {
      case Message(nick, message) =>
        display(nick, message)
      case ASCIIArtMessage(nick, message) =>
        display(nick, generator.printTextArt(message, ASCIIArtGenerator.ART_SIZE_MEDIUM))
      case _ =>
        println("Dont know how to display this message")
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
