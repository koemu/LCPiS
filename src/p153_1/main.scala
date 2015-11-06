import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParNonDeterministicOperation extends App {
  import scala.collection._
  import java.util.concurrent.atomic._

    def getHtmlSpec() = Future {
      val specSrc: Source = Source.fromURL("http://www.w3.org/MarkUp/html-spec/html-spec.txt")
      try specSrc.getLines.toArray finally specSrc.close()
    }

    getHtmlSpec() foreach { case specDoc =>
      val seqresult = specDoc.find(line => line.matches(".*TEXTAREA.*"))
      val parresult = specDoc.par.find(line => line.matches(".*TEXTAREA.*"))
      log(s"Sequential result - $seqresult")
      log(s"Parallel result   - $parresult")
    }

    Thread.sleep(10000)
}
