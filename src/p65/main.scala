import learningconcurrency._

object ExecutorsCreate extends App {
  import scala.concurrent._
  val executor = new java.util.concurrent.ForkJoinPool
  executor.execute(new Runnable {
    def run() = log("This task is run asynchronously.")
  })
  // Thread.sleep(500)
}
