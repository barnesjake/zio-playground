import sbt._

object Dependencies {

  object Version {
    val ZIOVersion = "1.0.8"
    val zioConfig = "1.0.5"
  }

  val zio = Seq(

    // ZIO
    "dev.zio" %% "zio"          % Version.ZIOVersion,
    "dev.zio" %% "zio-streams"  % Version.ZIOVersion,
    "dev.zio" %% "zio-test"     % Version.ZIOVersion % "test",
    "dev.zio" %% "zio-test-sbt" % Version.ZIOVersion % "test",
    // URL parsing
    "io.lemonlabs" %% "scala-uri" % "1.4.1",

    "dev.zio" %% "zio-config-magnolia" % Version.zioConfig,
    "dev.zio" %% "zio-config-typesafe" % Version.zioConfig
  )

  val scalaz = Seq(
    "org.scalaz" %% "scalaz-zio" % "0.3.1"
  )

  val logback = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  val projectDependencies: Seq[ModuleID] = zio ++ logback ++ scalaz
}