import learningconcurrency._
import chapter5._

object ParUid extends App {
  import scala.collection._
  import java.util.concurrent.atomic._
  private val uid = new AtomicLong(0L)

  val seqtime = timed {
    for (i <- 0 until 10000000) uid.incrementAndGet()
  }
  log(s"Sequential time $seqtime ms")

  val partime = timed {
    for (i <- (0 until 10000000).par) uid.incrementAndGet()
  }
  log(s"Parallel time $partime ms")

}
