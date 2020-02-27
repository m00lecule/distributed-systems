package messanger

import messanger.messages.{LogoutMessage, Message}

trait Processable extends Runnable {
  var isRunning = true;
  def readObject: AnyRef
  def processMessage(message: Message): Unit
  def processLogout: Unit

  final def processMessages: Unit = {
    val received = readObject
    received match {
      case mess: Message =>
        processMessage(mess)
      case mess: LogoutMessage =>
        processLogout
        isRunning = false
    }
}}
