import Common._
import sbt.Keys.libraryDependencies

name := "zio-playground"
version := "0.1"
scalaVersion := "2.12.12"

lazy val root =
  (project in file(".")).commonSettings.scalafmtSettings
    .settings(
      libraryDependencies ++= Dependencies.projectDependencies
    )

addCommandAlias("fmt", "; scalafmt; scalafmtSbt; test:scalafmt")
addCommandAlias("checkFormatAll", "; scalafmtSbtCheck; scalafmtCheck; test:scalafmtCheck")
addCommandAlias("compileAll", "; clean; compile; test:compile")
addCommandAlias("checkAll", "; checkFormatAll; compileAll; test")

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions in Compile in console := Seq(
  "-Ypartial-unification",
  "-language:higherKinds",
  "-language:existentials",
  "-Yno-adapted-args",
  "-Xsource:2.13",
  "-Yrepl-class-based",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-Yrangepos",
  "-feature",
  "-Xfuture",
  "-unchecked",
  "-Xlint:_,-type-parameter-shadow",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-opt-warnings",
  "-Ywarn-extra-implicit",
  "-Ywarn-unused:_,imports",
  "-Ywarn-unused:imports",
  "-opt:l:inline",
  "-opt-inline-from:<source>",
  "-Ypartial-unification",
  "-Yno-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit"
)