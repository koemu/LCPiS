import learningconcurrency._

object ExecutionContextCreate extends App {
  import scala.concurrent._
  val pool = new forkjoin.ForkJoinPool(2)
  val ectx = ExecutionContext.fromExecutorService(pool)
  ectx.execute(new Runnable {
    def run() = log("Running on the execution context again.")
  })
}
