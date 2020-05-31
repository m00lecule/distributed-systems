package actor.logger

import akka.actor.ActorContext

trait TLogger {
  val prefix: String;

  def log(str: String)(implicit context: ActorContext) = {
    context.system.log.info(s"[$prefix] $str")
  }
}
