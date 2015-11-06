import learningconcurrency._
import chapter5._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection._
import scala.io.Source


object ConcurrentWrong extends App {
  import scala.collection._
  import scala.concurrent.ExecutionContext.Implicits.global

  def getHtmlSpec() = Future {
      val specSrc: Source = Source.fromURL("http://www.w3.org/MarkUp/html-spec/html-spec.txt")
      try specSrc.getLines.toArray finally specSrc.close()
  }

  def getUrlSpec(): Future[Seq[String]] = Future {
        val f = Source.fromURL("http://www.w3.org/Addressing/URL/url-spec.txt")
        try f.getLines.toList finally f.close()
  }


  def intersection(a: GenSet[String], b: GenSet[String]): GenSet[String] = {
    val result = new mutable.HashSet[String]
    for (x <- a.par) if (b contains x) result.add(x)
    result
  }

  val ifut = for {
    htmlSpec <- getHtmlSpec()
    urlSpec <- getUrlSpec()
  } yield {
    val htmlWords = htmlSpec.mkString.split("\\s+").toSet
    val urlWords = urlSpec.mkString.split("\\s+").toSet
    intersection(htmlWords, urlWords)
  }

  ifut onComplete {
    case t => log(s"Result: $t")
  }

  Thread.sleep(5000)
}
