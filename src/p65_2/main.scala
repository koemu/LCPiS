import learningconcurrency._

object ExecutorsCreate extends App {
  import scala.concurrent._
  import java.util.concurrent.TimeUnit
  val executor = new java.util.concurrent.ForkJoinPool
  executor.execute(new Runnable {
    def run() = log("This task is run asynchronously.")
  })
  executor.shutdown()
  executor.awaitTermination(60, TimeUnit.SECONDS)
}
