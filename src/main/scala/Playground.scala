import zio.console._
import zio.{ExitCode, URIO, ZIO}

import java.io.IOException

object Playground extends zio.App {

  val appLogic: ZIO[Console, IOException, Unit] = {
    for {
      _ <- putStr("This is like print")
      _ <- putStr("\n")
      _ <- putStrLn("This is like println")
    } yield ()
  }

  val echo: ZIO[Console, IOException, Unit] = getStrLn.flatMap(line => putStrLn(line))

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    //    appLogic.exitCode
    echo.exitCode
  }
}
