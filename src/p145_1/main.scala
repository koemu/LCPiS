import learningconcurrency._
import chapter5._

object ParConfig extends App {
  import scala.collection._
  import scala.concurrent.forkjoin.ForkJoinPool

  val fjpool = new ForkJoinPool(2)
  val myTaskSupport = new parallel.ForkJoinTaskSupport(fjpool)
  val numbers = scala.util.Random.shuffle(Vector.tabulate(5000000)(i => i))
  val partime = timed {
    val parnumbers = numbers.par
    parnumbers.tasksupport = myTaskSupport
    val n = parnumbers.max
    println(s"largest number $n")
  }
  log(s"Parallel time $partime ms")
}
