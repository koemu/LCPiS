import learningconcurrency._
import chapter5._

object ParBasic extends App {
  import scala.collection._

  val numbers = scala.util.Random.shuffle(Vector.tabulate(5000000)(i => i))

  val seqtime = timed {
    val n = numbers.max
    println(s"largest number $n")
  }

  log(s"Sequential time $seqtime ms")

  val partime = timed {
    val n = numbers.par.max
    println(s"largest number $n")
  }

  log(s"Parallel time $partime ms")
}
