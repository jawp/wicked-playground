package wp.pigeonapp2

object Model {

  case class Pigeon(name: String, age: Int, status: Status)
  type PigeonId = Int

  var pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon("Lucy", 1, Landing),
    2 -> Pigeon("Bob", 2, Flying(Shitting)),
    3 -> Pigeon("Carmen", 2, Starting),
    4 -> Pigeon("Graham", 2, Standing(Pecking)),
    5 -> Pigeon("All", 2, Landing)
  )

  def generatePigeons: Seq[Pigeon] ={
    val names = List("Blake", "Krystle", "Carmen", "Alexis", "Fallon", "Steven", "Adam", "Amanda", "Jeffrey", "Claudia", "Dominique", "Farnsworth")
    val lastNames = List("Dexter", "Devereaux", "Fallmont", "Carrington", "Colby", "Bedford", "Colby Dexter", "Barrows")
    val ages = List(0, 1, 2, 3, 4, 5)
    for {
      name <- names
      lastName <-lastNames
      age <- ages
      status <- Status.all
    } yield Pigeon(s"$name $lastNames", age, status)
  }

  def nextId(): PigeonId = pigeons.keySet.max + 1

  def addPigeons(pigs: Pigeon*): Seq[PigeonId] = pigs.map{ p =>
    val id = nextId()
    pigeons  = pigeons + (id -> p)
    id
  }

  def removePigeon(): Unit = {
    pigeons = pigeons.tail
  }

  def removePigeon(id: PigeonId) = {
    pigeons = pigeons -  id
  }

  sealed trait Status
  case object Landing extends Status
  case object Starting extends Status
  case class Flying(action: Action) extends Status
  case class Standing(action: Action) extends Status

  object Status {
    val all: Seq[Status] = {
      val all0: Seq[Status] = Seq(Landing, Starting)
      val all1F: Seq[Action => Status] = Seq(Flying, Standing)
      val all1: Seq[Status] = for {
        action <- Action.all
        statusF <- all1F
      } yield statusF(action)
      all0 ++ all1
    }
  }

  sealed trait Action
  case object Pecking extends Action //dziobać
  case object Shitting extends Action

  object Action {
    val all = Seq(Pecking, Shitting)
  }

}
