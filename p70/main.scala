import learningconcurrency._

object AtomicUidCAS extends App {
  import scala.concurrent._
  import java.util.concurrent.atomic._
  import scala.annotation.tailrec
  private val uid = new AtomicLong(0L)

  @tailrec def getUniqueId(): Long = {
    val oldUid = uid.get
    val newUid = oldUid + 1
    if (uid.compareAndSet(oldUid, newUid)) newUid
    else getUniqueId()
  }

  def execute(body: =>Unit) = ExecutionContext.global.execute(
      new Runnable { def run() = body }
  )

  execute {
    log(s"Got a unique id asynchronously: $getUniqueId")
  }

  log(s"Got a unique id: $getUniqueId")
}
