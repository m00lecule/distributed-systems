package workers.representation

object Settings {
  val productionLine: Exchange = Exchange(name = "worker", route = "topic")
  val communicationLine: Exchange = Exchange(name = "message", route = "direct")
  val tasks: List[String] = List("task_queue", "task_queue_1", "xddd")
}
