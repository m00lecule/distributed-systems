package workers.settings

object Settings {
  val productionLine: Exchange = Exchange(name = "worker", route = "topic")
  val communicationLine: Exchange = Exchange(name = "message", route = "direct")
  val tasks: List[String] = List("task1", "task2", "task3")
  val IP: String = "localhost"
}
