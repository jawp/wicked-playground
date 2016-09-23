package wp

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.stream.{ActorMaterializer, Materializer}

import scala.util.Try
import scalaz.syntax.id._

class StreamsQuickStartGuide extends WickedSpec {

  //based on http://doc.akka.io/docs/akka/2.4.10/scala/stream/stream-quickstart.html#stream-quickstart-scala

  import akka.stream._
  import akka.stream.scaladsl._

  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.util.ByteString
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file.Paths


  "create and consume simple source" in {

    //Define source using some of convenient methods in Source object.
    //(akka.NotUsed tells that value auxiliary information is produced (just numbers))
    //Source is just a description of what you want to run, and like an architect’s blueprint it can be reused.
    val source: Source[Int, NotUsed] = Source(1 to 10)

    //In order to run stream(Source) the ActorMaterializer is needed.
    import SharedAkka._

    //In order to run stream you need to have a Sink
    //As soon as Source is connected to Sink the stream will flow
    //(and all side effects defined in Stream will occur)

    //There are many convenient methods for creating sinks.

    //Types does mater. You may connect to sink only streams of the same type.
    //If you emit elements of type A a Sink can slurp only items of the type A.

    //Create sink from function

    val sink: Sink[Int, Future[Done]] = Sink.foreach(i => println(s"1st: $i"))

    //Now we can connect Source to sink.
    //This is how you connect and in the same way run the stream.
    source.runWith(sink)


    //The same will can be achieved using runForeach which creates sink behind the scenes
    //Note that it's possible to "consume" from one source many times ...
    source.runForeach(i => println(s"2nd: $i"))
    source.runForeach(i => println(s"3rd: $i"))

  }

  "write result of a stream to file" in {
    val source: Source[Int, NotUsed] = Source(1 to 5)
    import SharedAkka._

    //Let's process a stream using 'scan'
    //This will create Source of factorials wrapped into ByteString
    //Mapping over items on a Source creates another Source
    //ByteString is IndexedSeq[Byte] thing
    val factorials: Source[ByteString, NotUsed] = source
      .scan(1)(_ * _)
      .map(num => ByteString(s"b$num\n"))

    //FileIO is akka utility for creating sikns related to File opeartions
    //Let's create sink which will operate on a file.
    val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("factorials.tmp.txt"))

    val result: Future[IOResult] = factorials
      //it the file exist - the new file is weirdly merged !
      //this is probably a bug because scaladoc tells that file will be overridden!
      .runWith(sink)

    result.futureValue.status.success.value mustBe Done
  }

  //tbc ...
}


object SharedAkka {
  implicit lazy val system = ActorSystem()

  //materialzer depeds on ActorRefFactory so it can be created within an actor
  implicit lazy val materializer: Materializer = akka.stream.ActorMaterializer()
}