import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParSideEffectsCorrect extends App {
  import scala.collection._
  import java.util.concurrent.atomic._

  def intSize(a: GenSet[Int], b: GenSet[Int]): Int = {
    val count = new AtomicInteger(0)
    for (x <- a) if (b contains x) count.incrementAndGet()
    count.get
  }
  val seqres = intSize((0 until 1000).toSet, (0 until 1000 by 4).toSet)
  val parres = intSize((0 until 1000).par.toSet, (0 until 1000 by 4).par.toSet)
  log(s"Sequential result - $seqres")
  log(s"Parallel result   - $parres")
}
