import fs2._

import _root_.io.circe._

import java.net.URL
import java.io.File

import cats.effect._
import cats.implicits._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.{attr, elementList}
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import net.ruippeixotog.scalascraper.model.Element

object Main extends IOApp {
  val browser: JsoupBrowser = JsoupBrowser.typed()
  val output = new File("artistPages.txt")
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
    //Stream.eval(artistPages)
  }

  def parseArtistPages: Pipe[IO, String, String] = ???
  def accumulateStats: Pipe[IO, Byte, Json] = ???
  def uploadFiles = ???

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
          .map(line => rawLineToArtistLink(line).toString())
          //.through(parseArtistPages)
          .through(text.utf8Encode)
          .through(io.file.writeAll(output.toPath, blocker))
          .compile
          .drain >> IO(println("Done!"))
      }
      .as(ExitCode.Success)
}
