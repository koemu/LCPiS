import learningconcurrency._


object AtomicUid extends App {
  import scala.concurrent._
  import java.util.concurrent.atomic._
  private val uid = new AtomicLong(0L)

  def getUniqueId(): Long = uid.incrementAndGet()

  def execute(body: =>Unit) = ExecutionContext.global.execute(
      new Runnable { def run() = body }
  )

  execute {
    log(s"Got a unique id asynchronously: ${getUniqueId()}")
  }

  log(s"Got a unique id: ${getUniqueId()}")
}
