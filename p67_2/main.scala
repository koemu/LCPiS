import learningconcurrency._

object ExecutionContextCreate extends App {
  import scala.concurrent._
  val pool = new forkjoin.ForkJoinPool(2)
  def execute(body: =>Unit) = ExecutionContext.global.execute(
      new Runnable{ def run = body }
  )

  execute {
      log("hoge")
  }
}
