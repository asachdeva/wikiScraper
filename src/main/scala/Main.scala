import fs2._

//import _root_.io.circe.syntax._

import java.io.File
import java.net.URL
import cats.effect._
import cats.implicits._

import org.http4s._
import org.http4s.client._
import org.http4s.client.blaze.BlazeClientBuilder

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.{attr, elementList}
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import net.ruippeixotog.scalascraper.model.Element

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  val browser: JsoupBrowser = JsoupBrowser.typed()
  val sourceUrl = new URL(
    "https://en.wikipedia.org/w/api.php?action=parse&page=List_of_American_Idol_finalists&format=json&prop=text"
  )

  val spanLinks: HtmlExtractor[Element, List[Option[String]]] =
    elementList("span") >?> attr("href")("a")

  def rawLineToArtistLink(input: String) = {
    val docGet = browser.parseString(input)
    val links = docGet >> spanLinks
    links.flatten.distinct
      .filter(_.contains("wiki/"))
      .map(link => link.replaceAll("""(/wiki/)|(\\\")|(\\\\)""", ""))
  }

  def summarizeArtistInfo(uris: List[Uri]) = {
    val artistSummaryHtmlList = scala.collection.mutable.ListBuffer[String]()
    val client: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global).resource
    uris.foreach { uri =>
      val data = client
        .use(_.expect[String](uri))
        .unsafeRunSync()
      val doc = browser.parseString(data)
      artistSummaryHtmlList += doc.toHtml
    }
    (artistSummaryHtmlList.toList)
  }

  def parseArtistPagesIntoUrls[A]: Pipe[IO, List[String], List[Uri]] =
    _.evalMap { pages =>
      val urls = scala.collection.mutable.ListBuffer[Uri]()
      pages
        .foreach { page =>
          val uriString =
            s"https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=$page&rvsection=0&rvslots=main"
          urls += Uri.unsafeFromString(uriString)
        }
      IO(urls.toList)
    }

  def accumulateResults: Pipe[IO, List[String], List[File]] = ???

  def uploadSummaries: Pipe[IO, List[File], Unit] = ???

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO]
      .use { blocker =>
        io
          .readInputStream(
            fis = IO.delay(sourceUrl.openStream()),
            chunkSize = 1024,
            blocker = blocker,
            closeAfterUse = true
          )
          .through(text.utf8Decode)
          .through(text.lines)
          .map(line => rawLineToArtistLink(line))
          .through(parseArtistPagesIntoUrls)
          .map(uris => summarizeArtistInfo(uris))
          //.through(accumulateResults)
          //.through(uploadSummaries)
          .compile
          .drain
      }
      .as(ExitCode.Success)
}
