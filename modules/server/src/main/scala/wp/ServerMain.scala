package wp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import wp.model._

import scala.io.StdIn

object ServerMain extends App {

  implicit val system = ActorSystem("serverMain")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val routes: Route =
    path("hello") {
      get {
        complete("world")
        //complete(
        //  ToResponseMarshallable.apply("world")(
        //    Marshaller.liftMarshaller(Marshaller.StringMarshaller)
        //  )
        //)
        //^^ expanded version
        //explanation:
        //complete("world") has type signature:
        //def complete(m: ⇒ ToResponseMarshallable): StandardRoute = ...
        //that means that in ToResponseMarshallable there are implicits which can be picked up, for example:
        //implicit def apply[A](_value: A)(implicit _marshaller: ToResponseMarshaller[A]): ToResponseMarshallable = ...
        //this implicit need another one, which is ToResponseMarshaller
        //but this is a type alias:
        //type ToResponseMarshaller[T] = Marshaller[T, HttpResponse]
        //so it need Marshaller[String, HttpResponse]
        //it will go to Marshaller companion object, where in extended LowPriorityToResponseMarshallerImplicits lives method:
        //implicit def liftMarshaller[T](implicit m: ToEntityMarshaller[T]): ToResponseMarshaller[T] = ...
        //which takes ToEntityMarshaller, which is another alias of Marshaller:
        //type ToEntityMarshaller[T] = Marshaller[T, MessageEntity]
        //there is implicit val StringMarshaller: ToEntityMarshaller[String] in PredefinedToEntityMarshallers
        //which is extended by Marshaller companion object
        //
        //Conclusion: Read the content of Marshaller companion object
      }
    } ~ path("hellohtml") {
      //In case if you don't want to use predefined marshaller provide your own implicit
      implicit val marshaller: ToEntityMarshaller[String] = Marshaller.stringMarshaller(MediaTypes.`text/html`)
      complete("<html><head></head><body><h1>World</h1><p>And this is html</p></body></html>")
    }

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println("Server started. Press [ENTER] to stop and exit")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}

object DB {
  val pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon(1, "Smarty"),
    2 -> Pigeon(23, "Lucy"),
    3 -> Pigeon(3, "Bobo")
  )
}