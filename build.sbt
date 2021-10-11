import Common._
import Dependencies._

name := "zio-playground"
version := "0.1"
scalaVersion := "2.12.12"

lazy val root =
  (project in file(".")).commonSettings.scalafmtSettings
    .settings(
      libraryDependencies ++= zio ++ logback
    )

addCommandAlias("fmt", "; scalafmt; scalafmtSbt; test:scalafmt")
addCommandAlias("checkFormatAll", "; scalafmtSbtCheck; scalafmtCheck; test:scalafmtCheck")
addCommandAlias("compileAll", "; clean; compile; test:compile")
addCommandAlias("checkAll", "; checkFormatAll; compileAll; test")