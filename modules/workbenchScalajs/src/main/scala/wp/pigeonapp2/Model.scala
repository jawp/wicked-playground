package wp.pigeonapp2
import rx.Rx.Dynamic
import rx._

object Model {

  type Store = Map[PigeonId, Pigeon]

  case class Pigeon(name: String, age: Int, status: Status)
  type PigeonId = Int

  def update(f: Store => Store) = {
    pigeons() = f(pigeons.now)
  }

  val pigeons: Var[Store] = Var {
    Map[PigeonId, Pigeon](
      1 -> Pigeon("Lucy", 1, Landing),
      2 -> Pigeon("Bob", 2, Flying(Shitting)),
      3 -> Pigeon("Carmen", 2, Starting),
      4 -> Pigeon("Graham", 2, Standing(Pecking)),
      5 -> Pigeon("All", 2, Landing)
    )
  }

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

  val nextId: Rx[PigeonId] = pigeons.map(_.keySet.max + 1)

  def addPigeon(p: Pigeon): PigeonId = {
    val id = nextId.now
    update(_ + (id -> p))
    id
  }

  def addPigeons(pigs: Pigeon*): Seq[PigeonId] = pigs.map(addPigeon)

  def removePigeon(): Option[(PigeonId, Pigeon)] = {
    val deleted: Option[(PigeonId, Pigeon)] = pigeons.now.headOption
    update(_.tail)
    deleted
  }

  def removePigeon(id: PigeonId): Option[(PigeonId, Pigeon)] = {
    val deleted = pigeons.now.get(id).map(p => (id, p))
    update(_ - id)
    deleted
  }

  def updatePigeon(id: PigeonId, f: Pigeon => Pigeon): Unit = update { ps =>
    ps.get(id).map(f).map(p => ps.updated(id, p)).getOrElse(ps)
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
