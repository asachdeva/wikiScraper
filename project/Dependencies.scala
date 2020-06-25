import sbt._

object Dependencies {

  object Versions {
    val circe = "0.13.0"
    val fastParse = "2.2.2"
    val fs2 = "2.4.2"
    val http4s = "0.21.4"
    val scalaScraper = "2.2.0"

    // Test
    val munitVersion = "0.7.9"

    // Compiler
    val kindProjector = "0.11.0"
    val betterMonadicFor = "0.3.1"

    // Runtime
    val logback = "1.2.3"
  }

  object Libraries {
    def circe(artifact: String, version: String): ModuleID = "io.circe" %% artifact % version
    def fs2(artifact: String, version: String): ModuleID = "co.fs2" %% artifact % version
    def http4s(artifact: String, version: String): ModuleID = "org.http4s" %% artifact % version

    // Compiler
    lazy val kindProjector = ("org.typelevel" %% "kind-projector" % Versions.kindProjector).cross(CrossVersion.full)
    lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor

    lazy val fastParse = "com.lihaoyi" %% "fastparse" % Versions.fastParse

    lazy val fs2Core = fs2("fs2-core", Versions.fs2)
    lazy val fs2IO = fs2("fs2-io", Versions.fs2)

    lazy val circeCore = circe("circe-core", Versions.circe)
    lazy val circeFs2 = circe("circe-fs2", Versions.circe)
    lazy val circeGeneric = circe("circe-generic", Versions.circe)
    lazy val circeLiteral = circe("circe-literal", Versions.circe)
    lazy val circeOptics = circe("circe-optics", Versions.circe)
    lazy val circeParser = circe("circe-parser", Versions.circe)

    lazy val http4sBlazeClient = http4s("http4s-blaze-client", Versions.http4s)
    lazy val http4sBlazeServer = http4s("http4s-blaze-server", Versions.http4s)
    lazy val http4sCore = http4s("http4s-blaze-client", Versions.http4s)
    lazy val http4sDsl = http4s("http4s-dsl", Versions.http4s)

    lazy val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % Versions.scalaScraper

    // Runtime
    lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    lazy val munit = "org.scalameta" %% "munit" % Versions.munitVersion
  }

}
