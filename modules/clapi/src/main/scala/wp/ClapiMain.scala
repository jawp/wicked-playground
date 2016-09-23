package wp

import wp.model._

object ClapiMain extends App {

  val pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon(1, "Smarty"),
    2 -> Pigeon(23, "Lucy"),
    3 -> Pigeon(3, "Bobo")
  )

  println("It works")
}

