import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ConcurrentTrieMap extends App {
  import scala.collection._

  val cache = new concurrent.TrieMap[Int, String]()
  for (i <- 0 until 100) cache(i) = i.toString

  for ((number, string) <- cache.par) cache(-number) = s"-$string"

  log(s"cache - ${cache.keys.toList.sorted}")
}
