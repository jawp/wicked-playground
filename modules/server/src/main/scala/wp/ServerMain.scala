package wp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import wp.model._

object ServerMain extends App {

  implicit val system = ActorSystem("serverMain")
  implicit val materializer = ActorMaterializer()

  val pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon(1, "Smarty"),
    2 -> Pigeon(23, "Lucy"),
    3 -> Pigeon(3, "Bobo")
  )

  println("It works")
}

