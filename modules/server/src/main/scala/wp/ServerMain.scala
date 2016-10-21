package wp

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller, ToResponseMarshallable}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.json4s.{DefaultFormats, jackson}
import wp.model._

import scala.concurrent.{ExecutionContext, Future, Promise}

object ServerMain extends App {

  implicit val system = ActorSystem("serverMain")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  // this is used to throttle the pinging from clients
  val registeredClientsIds = Agent(Set.empty[String])(system.dispatcher)

  val serverVersion = java.util.UUID.randomUUID().toString


  def routes(route: Route, routes: Route*) =
    routes.foldLeft(route)(_ ~ _)

  val route: Route =
    routes(
      pathSingleSlash(
        get(complete(ToResponseMarshallable(
          HttpEntity(ContentTypes.`text/html(UTF-8)`, IndexPage.render()))))
      ),
      path("frontend-launcher.js")(getFromResource("frontend-launcher.js")),
      path("frontend-fastopt.js")(getFromResource("frontend-fastopt.js")),
      post(
        path("register")(
          entity(as[String]) { clientId =>
            if (registeredClientsIds().contains(clientId)) {
              complete(Promise[String]().future)
            } else {
              registeredClientsIds.send(_ + clientId)
              complete(serverVersion)
            }
          }
        )
      ),
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
      }, path("hellohtml") {
        //In case if you don't want to use predefined marshaller provide your own implicit
        implicit val marshaller: ToEntityMarshaller[String] = Marshaller.stringMarshaller(MediaTypes.`text/html`)
        complete("<html><head></head><body><h1>World</h1><p>And this is html</p></body></html>")
      }, pathPrefix("upickle-pigeon" / IntNumber) { id =>

        //http://localhost:8080/upickle-pigeon/1
        //http://localhost:8080/upickle-pigeon/10

        val pigeon = Service.getPigeon(id)
        import de.heikoseeberger.akkahttpupickle.UpickleSupport._
        onSuccess(pigeon) {
          case Some(p) => complete(p)
          case None => complete(StatusCodes.NotFound)
        }
      }, pathPrefix("json4s-pigeon" / IntNumber) { id =>
        //http://localhost:8080/json4s-pigeon/1
        //http://localhost:8080/json4s-pigeon/10
        val pigeon = Service.getPigeon(id)
        implicit val serialization = jackson.Serialization // or native.Serialization
      implicit val formats = DefaultFormats
        import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
        onSuccess(pigeon) {
          case Some(p) => complete(p)
          case None => complete(StatusCodes.NotFound)
        }
      }
    )

  Http().bindAndHandle(route, "localhost", 8080)
}

object Service {

  private implicit val servicePool: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(1))

  def getPigeon(id: PigeonId): Future[Option[Pigeon]] = Future {
    DB.pigeons.get(id)
  }
}

object DB {

  val pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon(1, "Smarty"),
    2 -> Pigeon(23, "Lucy"),
    3 -> Pigeon(3, "Bobo")
  )
}