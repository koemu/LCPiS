import learningconcurrency._

object AtomicLock extends App {
  import scala.concurrent._
  import java.util.concurrent.atomic._
  private val lock = new AtomicBoolean(false)
  def mySynchronized(body: =>Unit): Unit = {
    while (!lock.compareAndSet(false, true)) {}
    try body
    finally lock.set(false)
  }

  def execute(body: =>Unit) = ExecutionContext.global.execute(
      new Runnable { def run() = body }
  )

  var count = 0
  for (i <- 0 until 10) execute {
    mySynchronized { count += 1 }
  }
  Thread.sleep(1000)
  log(s"Count is: $count")
}
