import zio.console._
import zio.{ExitCode, URIO, ZIO}

import java.io.IOException


object GuitarChordGame extends zio.App {

  def run(args: List[String]): URIO[Console, ExitCode] =
    myAppLogic.exitCode

  val echo = getStrLn.flatMap(line => putStrLn(line))

  val myAppLogic: ZIO[Console, IOException, Unit] =
    for {
      _ <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
      _ <- echo
    } yield ()
}
