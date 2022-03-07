import zio._
//https://github.com/jdegoes/functional-effects/blob/master/src/main/scala/net/degoes/zio/00-intro.scala
import scala.util.Try

object Learning

object ZIOModel {

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO companion object.
   */
  object ZIO {
    def succeed[A](a: => A): ZIO[Any, Nothing, A] = ZIO(_ => Right(a))

    def fail[E](e: => E): ZIO[Any, E, Nothing] = ZIO(_ => Left(e))

    def attempt[A](sideEffect: => A): ZIO[Any, Throwable, A] = ZIO(_ => Try(sideEffect).toEither)

    def environment[R]: ZIO[R, Nothing, R] = ZIO(r => Right(r))

    def access[R, A](f: R => A): ZIO[R, Nothing, A] = ZIO(r => Right(f(r)))

    def accessZIO[R, E, A](f: R => ZIO[R, E, A]): ZIO[R, E, A] = ZIO(r => f(r).run(r))
  }

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO class.
   */
  final case class ZIO[-R, +E, +A](run: R => Either[E, A]) {
    self =>
    def map[B](f: A => B): ZIO[R, E, B] = ZIO(r => self.run(r).map(f))

    def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
      ZIO(r1 => self.run(r1).flatMap(a => f(a).run(r1)))

    def zip[R1 <: R, E1 >: E, B](that: ZIO[R1, E1, B]): ZIO[R1, E1, (A, B)] =
      self.flatMap(a => that.map(b => a -> b))

    def either: ZIO[R, Nothing, Either[E, A]] = ZIO(r => Right(self.run(r)))

    def provide(r: R): ZIO[Any, E, A] = ZIO(_ => self.run(r))

    def orDie(implicit ev: E <:< Throwable): ZIO[R, Nothing, A] =
      ZIO(r => self.run(r).fold(throw _, Right(_)))
  }

  def printLine(line: String): ZIO[Any, Nothing, Unit] =
    ZIO.attempt(println(line)).orDie

  val readLine: ZIO[Any, Nothing, String] =
    ZIO.attempt(scala.io.StdIn.readLine()).orDie

  def unsafeRun[A](zio: ZIO[Any, Throwable, A]): A =
    zio.run(()).fold(throw _, a => a)

  /**
   * Run the following main function and compare the results with your
   * expectations.
   */
  def main(args: Array[String]): Unit =
    unsafeRun {
      printLine("Hello, what is your name?").flatMap(
        _ => readLine.flatMap(name => printLine(s"Your name is: ${name}"))
      )
    }
}

object ZIOTypes {
  type ??? = Nothing

  /*
        UIO[A]     — This is a type alias for ZIO[Any, Nothing, A],   which represents an effect that has no requirements, and cannot fail, but can succeed with an A.
        URIO[R, A] — This is a type alias for ZIO[R, Nothing, A],     which represents an effect that requires an R, and cannot fail, but can succeed with an A.
        Task[A]    — This is a type alias for ZIO[Any, Throwable, A], which represents an effect that has no requirements, and may fail with a Throwable value, or succeed with an A.
        RIO[R, A]  — This is a type alias for ZIO[R, Throwable, A],   which represents an effect that requires an R, and may fail with a Throwable value, or succeed with an A.
        IO[E, A]   — This is a type alias for ZIO[Any, E, A],         which represents an effect that has no requirements, and may fail with an E, or succeed with an A.
   */

  /**
   * EXERCISE
   *
   * Provide definitions for the ZIO type aliases below.
   */
  type Task[+A] = ZIOModel.ZIO[Any, Throwable, A]
  type UIO[+A] = ZIOModel.ZIO[Any, Nothing, A]
  type RIO[-R, +A] = ZIOModel.ZIO[R, Throwable, A]
  type IO[+E, +A] = ZIOModel.ZIO[Any, E, A]
  type URIO[-R, +A] = ZIOModel.ZIO[R, Nothing, A]
}

object SuccessEffect extends App {

  val successExitCode: ExitCode = ExitCode.success

  /**
   * EXERCISE
   *
   * Using `ZIO.succeed`, create an effect that succeeds with a success
   * `ExitCode`.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    ZIO.succeed(1).exitCode
}

object HelloWorld extends App {

  import zio.console.putStrLn

  /**
   * EXERCISE
   *
   * Implement a simple "Hello World!" program using the effect returned by
   * `printLine`, using `ZIO#exitCode` method to transform the `printLine`
   * effect into another one that produces an exit code.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    putStrLn("Hello world!").exitCode
}

object SimpleMap extends App {

  import zio.console.getStrLn

  /**
   * EXERCISE
   *
   * Using `ZIO#map`, map the string success value of `readLine` into an
   * integer (the length of the string), and then further map that
   * into a constant exit code by using `ZIO#as`.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    getStrLn.map(x => x.length).exitCode
}

object PrintSequenceZip extends App {

  import zio.console.putStrLn

  /**
   * EXERCISE
   *
   * Using `zip`, compose a sequence of `printLine` effects to produce an effect
   * that prints three lines of text to the console.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    putStrLn("hello zip1")
      .zip(putStrLn("hello zip2"))
      .zip(putStrLn("hello zip3"))
      .exitCode
}

object PrintSequence extends App {

  import zio.console.putStrLn

  /**
   * EXERCISE
   *
   * Using `*>` (`zipRight`), compose a sequence of `printLine` effects to
   * produce an effect that prints three lines of text to the console.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    putStrLn("print sequence1")
      .*>(putStrLn("print sequence2"))
      .*>(putStrLn("print sequence3")).exitCode
}

object PrintReadSequence extends App {
  import zio.console.{putStrLn,getStrLn}
  /**
   * EXERCISE
   *
   * Using `*>` (`zipRight`), sequentially compose a `printLine` effect, which
   * models printing out "Hit Enter to exit...", together with a `readLine`
   * effect, which models reading a line of text from the console.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    (putStrLn("Hit Enter to exit...") *> getStrLn).exitCode
}

//object SimpleDuplication extends App {
//  import ZIOModel.printLine
//  /**
//   * EXERCISE
//   *
//   * In the following program, the expression `printLine("Hello again")`
//   * appears three times. Factor out this duplication by introducing a new
//   * value that stores the expression, and then referencing that variable
//   * three times.
//   */
//  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
//    printLine("Hello") *>
//    printLine("Hello again") *>
//    printLine("Hello again") *>
//    printLine("Hello again")
//  }.exitCode
//}
//
//object FlatMap extends App {
//  import ZIOModel.{printLine, readLine }
//
//  /**
//   * EXERCISE
//   *
//   * The following program is intended to ask the user for their name, then
//   * read their name, then print their name back out to the user. However,
//   * the `zipRight` (`*>`) operator is not powerful enough to solve this
//   * problem, because it does not allow a _subsequent_ effect to depend
//   * on the success value produced by a _preceding_ effect.
//   *
//   * Solve this problem by using the `ZIO#flatMap` operator, which composes
//   * a first effect together with a "callback", which can return a second
//   * effect that depends on the success value produced by the first effect.
//   */
//  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
//    printLine("What is your name?") *>
//    readLine *> // Use .flatMap(...) here
//    printLine("Your name is: ")
//  }.exitCode
//}

//object PromptName extends App {
//  import ZIOModel.{printLine, readLine }
//
//  /**
//   * EXERCISE
//   *
//   * The following program uses a combination of `zipRight` (`*>`), and
//   * `flatMap`. However, this makes the structure of the program harder
//   * to understand. Replace all `zipRight` by `flatMap`, by ignoring the
//   * success value of the left hand effect.
//   */
//  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
//    printLine("What is your name?") *>
//    readLine.flatMap(name => printLine(s"Your name is: ${name}"))
//  }.exitCode
//
//
//  /**
//   * EXERCISE
//   *
//   * Implement a generic "zipRight" that sequentially composes the two effects
//   * using `flatMap`, but which succeeds with the success value of the effect
//   * on the right-hand side.
//   */
//  def myZipRight[R, E, A, B](
//                              left: ZIO[R, E, A],
//                              right: ZIO[R, E, B]
//                            ): ZIO[R, E, B] =
//    ???
//}

//object ForComprehension extends App {
//  import ZIOModel.{printLine, readLine }
//
//  /**
//   * EXERCISE
//   *
//   * Rewrite the following program to use a `for` comprehension.
//   */
////  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
////    printLine("What is your name?")
////      .flatMap(
////        _ => readLine.flatMap(name => printLine(s"Your name is: ${name}"))
////      )
////      .exitCode
//}

//object ForComprehensionBackward extends App {
//  import Console.readLine
//
//  val readInt = readLine.flatMap(string => ZIO(string.toInt)).orDie
//
//  /**
//   * EXERCISE
//   *
//   * Rewrite the following program, which uses a `for` comprehension, to use
//   * explicit `flatMap` and `map` methods. Note: each line of the `for`
//   * comprehension will translate to a `flatMap`, except the final line,
//   * which will translate to a `map`.
//   */
//  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
//    for {
//      _   <- printLine("How old are you?")
//      age <- readInt
//      _ <- if (age < 18) printLine("You are a kid!")
//      else printLine("You are all grown up!")
//    } yield ()
//  }.exitCode
//}

object NumberGuesser extends App {

  import ZIOModel.printLine

  def analyzeAnswer(random: Int, guess: String) =
    if (random.toString == guess.trim) printLine("You guessed correctly!")
    else printLine(s"You did not guess correctly. The answer was ${random}")

  /**
   * EXERCISE
   *
   * Choose a random number (using `nextInt`), and then ask the user to guess
   * the number (using `readLine`), feeding their response to `analyzeAnswer`,
   * above.
   */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    ???
}

object SingleSideEffect extends App {

  /**
   * EXERCISE
   *
   * Using ZIO.attempt, convert the side-effecting of `println` into a pure
   * functional effect.
   */
  def myPrintLn(line: String): Task[Unit] = ???

  def run(args: List[String]) =
    myPrintLn("Hello World!").exitCode
}

object MultipleSideEffects extends App {

  /**
   * Using `ZIO.attempt`, wrap Scala's `println` method to lazily convert it
   * into a functional effect, which describes the action of printing a line
   * of text to the console, but which does not actually perform the print.
   */
  def printLine(line: String): Task[Unit] = ???

  /**
   * Using `ZIO.attempt`, wrap Scala's `scala.io.StdIn.readLine()` method to
   * lazily convert it into a functional effect, which describes the action
   * of printing a line of text to the console, but which does not actually
   * perform the print.
   */
  val readLine: Task[String] = ???

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    for {
      _ <- printLine("Hello, what is your name?")
      name <- readLine
      _ <- printLine(s"Good to meet you, ${name}!")
    } yield ()
  }.exitCode
}