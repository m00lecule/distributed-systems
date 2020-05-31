package http

import akka.actor.{ActorRef}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import message.{ClientRequest, ClientResponse, ClientSearchResponse}

import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._

object Routes {

  implicit val clientResponseFormat = jsonFormat3(ClientResponse)
  implicit val clientSearchResponseFormat = jsonFormat2(ClientSearchResponse)

  def getRoutes(server: ActorRef, search: ActorRef): Route =
    concat(
      pathPrefix("price") {
        concat(
          path(Segment) { name =>
            concat(
              get {
                implicit val timeout: Timeout = 5.seconds
                val bids: Future[ClientResponse] = (server ? ClientRequest(name)).mapTo[ClientResponse]
                complete(bids)
              },

            )
          })
      },
      pathPrefix("search") {
        concat(
          path(Segment) { name =>
            concat(
              get {
                implicit val timeout: Timeout = 5.seconds
                val bids: Future[ClientSearchResponse] = (search ? ClientRequest(name)).mapTo[ClientSearchResponse]
                complete(bids)
              },
            )
          })
      }
    )

  def apply(server: ActorRef, search: ActorRef) = getRoutes(server, search)
}
