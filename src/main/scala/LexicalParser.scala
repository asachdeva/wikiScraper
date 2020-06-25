import fastparse.NoWhitespace._
import fastparse._

trait LexicalParser {

  def newline[_: P]: P[Unit] = P("\n" | "\r\n" | "\r" | "\f")
  def invisible[_: P]: P[Unit] = P(" " | "\t" | newline)
  def comment[_: P]: P[Unit] = P("--" ~ (!newline ~ AnyChar).rep ~ (newline | &(End)))
  implicit val whitespace: P[_] => P[Unit] = { implicit ctx: ParsingRun[_] => (comment | invisible).rep }

  def keyword[_: P](k: String): P[Unit] = P(IgnoreCase(k))
  def digit[_: P]: P[Unit] = P(CharIn("0-9"))
  def integer[_: P]: P[Int] = P(digit.repX(1).!.map(_.toInt))
  def character[_: P]: P[Unit] = P(CharIn("a-zA-Z"))
  def identifier[_: P]: P[Unit] = P(character ~~ P(character | digit | "_").repX)
  def escapedIdentifier[_: P]: P[String] = P(identifier.! | ("`" ~~ CharsWhile(_ != '`').! ~~ "`"))
  def label[_: P]: P[String] = P(":" ~ (identifier.! | escapedIdentifier))

  def parseArtistWikiText(data: String): String =
    // val stripRE = """(\{\{hlist\|)|(\|\}\})""".r
    if (data.contains("label")) {
      val (_, labelStem) = data.splitAt(data.indexOf("label"))
      if (labelStem.indexOf("\\n|") == -1) {
        val labelString = labelStem.substring(0, labelStem.indexOf("\\n}}")).replaceAll("""\{\{Hlist\|""", "")
        labelString
      } else {
        val labelString = labelStem
          .substring(0, labelStem.indexOf("\\n|"))
          .replaceAll("""(\{\{Hlist\|)|(\}\})|(\{\{hlist\|)|(\{\{Flatlist\|\\n\*)|(\{\{flatlist\|\\n)""", "")
        labelString
      }
    } else ""

}
