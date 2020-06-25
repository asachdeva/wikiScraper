import scala.io.Source

class WikiScraperSpec extends munit.FunSuite with LexicalParser {
  test("Parse Artist Info WikiText to Extract Label Info") {
    val source = Source.fromFile("src/test/resources/badLabel.txt", "utf-8").mkString("")
    val (a, b) = source.splitAt(source.indexOf("label"))
    if (b.indexOf("\\n|") == -1) {
      val sub = b.substring(0, b.indexOf("\\n}}"))
      println("DDDD -- " + sub)
    } else {
      val sub = b.substring(0, b.indexOf("\\n|"))
      println("DDDD -- " + sub)
    }
    println("AAAAA -- " + a)
    println("BBBBB --" + b)
    // println("DDDDDD --" + sub)

  }

  test("Parse out extrs characters") {
    val sampleLabel1 = """label =  [[Brand CR Music]] (Independent)<br/>Authentik Artists (Independent)"""
    val sampleLabel2 =
      """label = [[Sony/ATV Music Publishing]], [[RED Distribution]], [[Hickory Records|Hickory]](2006-2011), TRP Records (2006-2011), [[Fontana Distribution]], [[Avex Group]] (Japan only))"""
    val sampleLabel3 =
      """ label           = [[BNA Records|BNA]]|[[19 Entertainment|19 Recordings]]|<!--don't list Columbia/Columbia Nashville as Pickler was dropped before Columbia Nashville's acquisition of BNA-->|[[Black River Entertainment]]"""
    val sampleLabel4 = "label = Verge (2006\u20132010)<br/>Elite B Records (2010)"
    println(sampleLabel1 + sampleLabel2 + sampleLabel3 + sampleLabel4)
  }

}
