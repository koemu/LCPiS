import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParNonParallelizableOperations extends App {
  def getHtmlSpec() = Future {
    val specSrc: Source = Source.fromURL("http://www.w3.org/MarkUp/html-spec/html-spec.txt")
    try specSrc.getLines.toArray finally specSrc.close()
  }

  getHtmlSpec() foreach { case specDoc =>
    def allMatches(d: GenSeq[String]) = warmedTimed() {
      val results = d.foldLeft("")((acc, line) => if (line.matches(".*TEXTAREA.*")) s"$acc\n$line" else acc)
    }

    val seqtime = allMatches(specDoc)
    log(s"Sequential time - $seqtime ms")

    val partime = allMatches(specDoc.par)
    log(s"Parallel time   - $partime ms")

  }
  Thread.sleep(10000)
}
