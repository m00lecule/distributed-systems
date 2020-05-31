package actor.search

import actor.logger.TLogger
import akka.actor.{Actor, Props}
import message.{ServerRequest, ServerSearchResponse}
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class SearchHandlerActor(val Id: Int) extends Actor with TLogger {

  override val prefix = s"SearchHandlerActor $Id";

  override def receive: Receive = {
    case ServerRequest(id, name) => {
      log(s"Received search task id: $id for $name")
      val href = searchItemURL(name)
      var pros = ""

      if (!href.isEmpty) {
        pros = getProsFromURL(SearchHandlerActor.root + href)
      }

      sender() ! ServerSearchResponse(id = id, pros = pros)
      log(s"Responded for search task id: $id name: $name pros: $pros")
    }
  }

  private def searchItemURL(name: String) = {
    val doc = Jsoup.connect(searchURL(name)).get();
    val els: Elements = doc.getElementsByClass(SearchHandlerActor.firstDivClass);
    els.attr("href");
  }

  private def searchURL(name: String): String = {
    SearchHandlerActor.root + searchPath(name)
  }

  private def searchPath(name: String): String = {
    val replaced = name.replace(" ", "+")
    s"/?szukaj=$replaced&s=2"
  }

  private def getProsFromURL(url: String) = {
    val doc = Jsoup.connect(url).get();
    doc.getElementsByClass(SearchHandlerActor.prosClass).text();
  }
}

object SearchHandlerActor {
  val root: String = "https://www.opineo.pl";
  val firstDivClass = "divollo"
  val prosClass = "ph_asset ph_pros";

  def apply(Id: Int): Props = Props(new SearchHandlerActor(Id))
}