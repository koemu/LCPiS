package org.learningconcurrency

object PromisesCancellation extends App {
  import scala.concurrent._
  import ExecutionContext.Implicits.global

  def cancellable[T](b: Future[Unit] => T): (Promise[Unit], Future[T]) = {
    val p = Promise[Unit]
    val f = Future {
      val r = b(p.future)
      if (!p.tryFailure(new Exception))
        throw new CancellationException
      r
    }
    (p, f)
  }

  val (cancel, value) = cancellable { cancel =>
    var i = 0
    while (i < 5) {
      if (cancel.isCompleted) throw new CancellationException
      Thread.sleep(500)
      log(s"$i: working")
      i += 1
    }
    "resulting value"
  }

  Thread.sleep(1500)

  cancel trySuccess ()

  log("computation cancelled!")
}
