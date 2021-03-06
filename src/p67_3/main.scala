import learningconcurrency._


object ExecutionContextSleep extends App {
  import scala.concurrent._

  def execute(body: =>Unit) = ExecutionContext.global.execute(
      new Runnable { def run() = body }
  )

  for (i <- 0 until 32) execute {
    Thread.sleep(2000)
    log(s"Task $i completed.")
  }
  Thread.sleep(10000)
}
