import zio.console.{Console, putStrLn}
import zio.{ExitCode, URIO, ZIO}

import java.io.IOException

//object Effects extends zio.App {
//
//  override def run(args: List[String]) = for {
//    env <- ZIO.environment[Int]
//    _   <- putStrLn(s"The value of the environment is: $env")
//  } yield
//}
