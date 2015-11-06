import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParDeterministicOperation extends App {
  import scala.collection._
  import java.util.concurrent.atomic._

    def getHtmlSpec() = Future {
      val specSrc: Source = Source.fromURL("http://www.w3.org/MarkUp/html-spec/html-spec.txt")
      try specSrc.getLines.toArray finally specSrc.close()
    }

    getHtmlSpec() foreach { case specDoc =>
      val seqresult = specDoc.find(line => line.matches(".*TEXTAREA.*"))
      log(s"Sequential result - $seqresult")

      val index = specDoc.par.indexWhere(_.matches(".*TEXTAREA.*"))
      val parresult = if (index != 1) Some(specDoc(index)) else None
      log(s"Parallel result   - $parresult")
    }

    Thread.sleep(10000)
}
