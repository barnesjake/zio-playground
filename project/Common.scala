import org.scalafmt.sbt.ScalafmtPlugin
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import sbt.Keys.{logBuffered, _}
import sbt._

object Common {

  implicit class ProjectFrom(project: Project) {

    def commonSettings: Project =
      project
        .settings(
          name := "zio-playground",
          scalaVersion := "2.13.5",
          version := "0.1",
          scalacOptions in (Compile, console) --= Seq("-Wunused:imports", "-Werror"),
          cancelable in Global := true,
          parallelExecution in Test := true,
          fork := true,
          logBuffered in Test := false,
          testOptions in Test += Tests.Argument("-oDF"),
          testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
        )

    def scalafmtSettings: Project =
      project
        .enablePlugins(ScalafmtPlugin)
        .settings(scalafmtOnCompile := false)
  }
}