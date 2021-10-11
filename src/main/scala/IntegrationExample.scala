import zio._

object IntegrationExample {
  val runtime: Runtime[zio.ZEnv] = Runtime.default

  runtime.unsafeRun(Task(println("Hello World!")))
}