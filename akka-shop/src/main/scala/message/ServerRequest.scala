package message

case class ServerRequest(id:Int, name:String)
case class ServerResponse(id:Int, name:String, price: Float)
case class ServerTimeout(id:Int)
case class ServerCountResponse(id:Int, count:Int)

case class ClientRequest(name: String)
case class ClientResponse(name: String, price: Option[Float], counter: Option[Int])

case class ClientSearchResponse(name: String,  pros: String)
case class ServerSearchResponse(id: Int, pros: String)

case class Query(name: String)

