package wp.pigeonapp1

object PigeonModel {

  sealed trait Status
  case object Landing extends Status
  case object Starting extends Status
  case class Flying(action: Action) extends Status
  case class Standing(action: Action) extends Status

  sealed trait Action
  case object Pecking extends Action //dziobać
  case object Shitting extends Action

  case class Pigeon(name: String, age: Int, status: Status)
  type PigeonId = Int

  var pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon("Lucy", 1, Landing),
    2 -> Pigeon("Bob", 2, Flying(Shitting)),
    3 -> Pigeon("Carmen", 2, Starting),
    4 -> Pigeon("Graham", 2, Standing(Pecking)),
    5 -> Pigeon("All", 2, Landing)
  )

  def nextId(): PigeonId = pigeons.keySet.max + 1

  def addPigeon(pigeon: Pigeon): PigeonId = {
    val id = nextId()
    pigeons  = pigeons + (id -> pigeon)
    id
  }

  def removePigeon(): Unit = {
    pigeons = pigeons.tail
  }
}
