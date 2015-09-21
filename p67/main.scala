import learningconcurrency._

object ExecutionContextCreate extends App {
  import scala.concurrent._
  val ectx = ExecutionContext.fromExecutorService(new forkjoin.ForkJoinPool)
  ectx.execute(new Runnable {
    def run() = log("Running on the execution context again.")
  })
}
