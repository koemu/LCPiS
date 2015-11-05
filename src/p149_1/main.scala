import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParNonParallelizableCollections extends App {
  import scala.collection._

  val list = List.fill(1000000)("")
  val vector = Vector.fill(1000000)("")
  log(s"list conversion time: ${timed(list.par)} ms")
  log(s"vector conversion time: ${timed(vector.par)} ms")
}
