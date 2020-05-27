package Messages

case class ServerRequest(id:Int, name:String)
case class ServerResponse(id:Int, name:String, price: Float)
case class ServerTimeout(id:Int)
case class ServerCountResponse(id:Int, count:Int)

case class ClientRequest(name: String)
case class ClientResponse(price: String, counter: Option[Int])

case class Query(name: String)

