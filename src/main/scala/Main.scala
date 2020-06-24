import fs2._

import _root_.io.circe.parser.{parse => circeParse}
import _root_.io.circe._
import _root_.io.circe.optics.JsonPath._

import java.net.URL
import java.io.File

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

object Main extends IOApp with LexicalParser {

  val labelCounts = "labels.json"
  val summaryCounts = "summary.json"

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

  def summarizeArtistInfo(uris: List[String]) = {
    val mutableMap = new scala.collection.mutable.HashMap[String, List[String]]
    val client: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global).resource
    uris.foreach { uri =>
      mutableMap.addOne((uri, List.empty))
      val uriString =
        s"https://en.wikipedia.org/w/api.php?action=parse&format=json&page=$uri&prop=wikitext&section=0"
      val URL = Uri.unsafeFromString(uriString)
      val data = client
        .use(_.expect[String](URL))
        .unsafeRunSync()

      val _wikitext = root.parse.wikitext.*.string
      val wikitext = _wikitext.getOption(circeParse(data).getOrElse(Json.Null)).getOrElse("")
      println(wikitext.toString.stripMargin)
      // TODO Parse String/json and aggregate map of Artist to record labels
      // TODO Build up files to upload and write to output
      val files = List.empty
      Stream.eval(IO(files))
    }
  }

  def parseArtistPagesIntoUrls[A]: Pipe[IO, List[String], List[String]] =
    _.evalMap { pages =>
      val urls = scala.collection.mutable.ListBuffer[String]()
      pages
        .foreach { page =>
          urls += page
        }
      IO(urls.toList)
    }

  def uploadFiles[A]: Pipe[IO, List[File], Unit] = ???

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
          //.through(uploadSummaryFiles)
          .compile
          .drain >> (IO(println("Done!")))
      }
      .as(ExitCode.Success)
}
