package http

import actor.server.ServerActor
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import message.{ClientRequest, ClientResponse, ClientSearchResponse}
import actor.search.SearchLoadBalancerActor
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object WebServer {

  implicit val clientResponseFormat = jsonFormat3(ClientResponse)
  implicit val clientSearchResponseFormat = jsonFormat2(ClientSearchResponse)

  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val server = system.actorOf(ServerActor(2))
    val search = system.actorOf(Props(new SearchLoadBalancerActor()))

    val route: Route =
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

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}