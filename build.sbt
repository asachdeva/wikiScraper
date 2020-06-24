import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
import com.scalapenos.sbt.prompt._
import Dependencies._

name := """wikiScraper"""
organization in ThisBuild := "asachdeva"

val format = taskKey[Unit]("Format files using scalafmt and scalafix")

promptTheme := PromptTheme(
  List(
    text(_ => "[wikiScraper]", fg(64)).padRight(" λ ")
  )
)

val MUnitFramework = new TestFramework("munit.Framework")
lazy val testSettings: Seq[Def.Setting[_]] = List(
  Test / parallelExecution := false,
  skip.in(publish) := true,
  fork := true,
  testFrameworks := List(MUnitFramework),
  testOptions.in(Test) ++= {
    List(Tests.Argument(MUnitFramework, "+l", "--verbose"))
  }
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

lazy val `wikiScraper` = project
  .in(file("."))
  .settings(
    testSettings,
    organization := "asachdeva",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      Libraries.circeCore,
      Libraries.circeFs2,
      Libraries.circeGeneric,
      Libraries.circeLiteral,
      Libraries.circeParser,
      Libraries.fs2Core,
      Libraries.fs2IO,
      Libraries.http4sBlazeClient,
      Libraries.http4sBlazeServer,
      Libraries.http4sCore,
      Libraries.http4sDsl,
      Libraries.scalaScraper
    ),
    addCompilerPlugin(Libraries.betterMonadicFor),
    format := {
      Command.process("scalafmtAll", state.value)
      Command.process("scalafmtSbt", state.value)
    }
  )

// CI build
addCommandAlias("buildwikiScraper", ";clean;+test;")
