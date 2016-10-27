package howitworks.akka

import akka.actor.ActorSystem
import akka.stream.Materializer


object SharedAkka {
  implicit lazy val system = ActorSystem()

  //materialzer depeds on ActorRefFactory so it can be created within an actor
  implicit lazy val materializer: Materializer = akka.stream.ActorMaterializer()
}
