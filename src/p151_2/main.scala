import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParSideEffectsIncorrect extends App {
  import scala.collection._

  def intSize(a: GenSet[Int], b: GenSet[Int]): Int = {
    var count = 0
    for (x <- a) if (b contains x) count += 1
    count
  }
  val seqres = intSize((0 until 1000).toSet, (0 until 1000 by 4).toSet)
  val parres = intSize((0 until 1000).par.toSet, (0 until 1000 by 4).par.toSet)
  log(s"Sequential result - $seqres")
  log(s"Parallel result   - $parres")
}
