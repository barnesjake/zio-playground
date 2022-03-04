import zio.console.Console
import zio.{ExitCode, IO, Task, UIO, URIO, ZIO}

import java.io.IOException

object Datatypes extends zio.App {

  val getStrLn: ZIO[Console, IOException, String] = ZIO.accessM(_.get.getStrLn)

  val s1: UIO[Int] = ZIO.succeed(42)
  val s2: Task[Int] = Task.succeed(42)
  /**
   * succeed is similar to effectTotal.
   * succeed takes by-name parameter.
   * succeed is intended for values which do not have any side effects.
   * If we know the value does have side effects we should consider using effectTotal for clarity.
   * The value inside a successful effect constructed with ZIO.effectTotal will only be constructed if absolutely required.
   */
  val now: UIO[Long] = ZIO.effectTotal(System.currentTimeMillis())

  val f1 = ZIO.fail("Uh oh!")
  val f2 = Task.fail(new Exception("Uh oh!"))

  /**
   * From values
   * ZIO contains constructors which help us convert various data types into the ZIO effect
   */
  val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
  val zoption2: IO[String, Int] = zoption.mapError(_ => "It wasn't there!")

  val zoption3: UIO[Option[Int]] = ZIO.some(2)
  val zoption4: UIO[Option[String]] = ZIO.some("String")
  val zoption5: UIO[Option[Nothing]] = ZIO.none
  val x = Option(null)
  val y = Option(1)
  val zoption6 = ZIO.getOrFail(x)
  val zoption7: IO[Unit, Null] = ZIO.getOrFailUnit(x)
  val zoption8: IO[Option[Null], Int] = ZIO.getOrFailWith(x)(y)


  final case class User(name: String, teamId: String)

  final case class Team(name: String)

  val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))

  def getUser(userId: String): IO[Throwable, Option[User]] = ???

  def getTeam(teamId: String): IO[Throwable, Team] = ???


  val result: IO[Throwable, Option[(User, Team)]] = (for {
    id <- maybeId
    user <- getUser(id).some
    team <- getTeam(user.teamId).asSomeError
  } yield (user, team)).optional

  /**
   * Either
   */
  val zeither = ZIO.fromEither(Right("Success!"))

  /**
   * Try
   */

  import scala.util.Try

  val ztry = ZIO.fromTry(Try(42 / 0))

  /**
   * Function
   */
  val zfun: URIO[Int, Int] = ZIO.fromFunction((i: Int) => i * i)

  /**
   * Future
   */

  import scala.concurrent.Future

  lazy val future: Future[String] = Future.successful("Hello!")
  val zfuture: Task[String] =
    ZIO.fromFuture { implicit ec =>
      future.map(_ => "Goodbye!")
    }


  def run(args: List[String]): URIO[Console, ExitCode] = {
    getStrLn.exitCode
  }

}
