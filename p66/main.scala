import learningconcurrency._


object ExecutionContextGlobal extends App {
  import scala.concurrent._
  val ectx = ExecutionContext.global
  ectx.execute(new Runnable {
    def run() = log("Running on the execution context.")
  })
}
