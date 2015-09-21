object ThreadsUnprotected extends App {
    def log(msg: String): Unit = println(s"${Thread.currentThread.getName}: $msg")

    def thread(body: => Unit): Thread = {
        val t = new Thread {
            override def run() = body
        }
        t.start()
        t
    }

    var uidCount = 0L
    def getUniqueid() = {
        val freshUid = uidCount + 1
        uidCount = freshUid
        freshUid
    }
    def printUniqueIds (n: Int): Unit = {
        val uids = for(i<- 0 until n) yield getUniqueid()
        log(s"Generated UIDs: $uids")
    }
    val t = thread{printUniqueIds(5)}
    printUniqueIds(5)
    t.join()
}
