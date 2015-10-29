import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source

object ParHtmlSpecSearch extends App {

  def getHtmlSpec() = Future {
    val url = "http://www.w3.org/MarkUp/html-spec/html-spec.txt"
    log(s"Getting $url")
    val src = Source.fromURL(url, "utf-8")
    try src.getLines.toArray finally src.close()
  }

  getHtmlSpec() foreach { case specDoc =>
    def search(d: GenSeq[String]) = warmedTimed() {
      d.indexWhere(line => line.matches(".*TEXTAREA.*"))
    }

    val seqtime = search(specDoc)
    log(s"Sequential time $seqtime ms")

    val partime = search(specDoc.par)
    log(s"Parallel time $partime ms")
  }

  Thread.sleep(10000)   // 来れないと早く終わり過ぎる
}
