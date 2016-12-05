package howitworks.macwiree

class HelloMacWire extends wp.Spec {

  "let it wire" in {
    val module = new UserModule {}
    module.usr.readStats mustBe "Stats for Sarah Kerrigan: -> .... <stats>"
  }

  "let it override wires" in {
    val module = new UserModule {
      import Types._
      override lazy val da: DatabaseAccess = new DatabaseAccess {
        override def getUserFromDB(id: Id): Option[(Id, Name, Surname)] = Some((1L, "Infested", "Kerrigan"))
      }
    }
    module.usr.readStats mustBe "Stats for Infested Kerrigan: -> .... <stats>"
  }


}


object Types {
  type Id = Long
  type Name = String
  type Surname = String
}
class DatabaseAccess() {
  import Types._

  private var persons: Map[Id, (Id, Name, Surname)] = Map[Id, (Id, Name, Surname)](
    0L -> (0L, "Jim", "Raynor"),
    1L -> (1L, "Sarah", "Kerrigan"),
    2L -> (2L, "Arcturus", "Mengsk")
  )

  def getUserFromDB(id: Id): Option[(Id, Name, Surname)] = persons.get(id)
}

class SecurityFilter() {
  import Types._
  private val classified: List[Id] = List(2)
  def isClassified(id: Id): Boolean = classified.contains(id)
}

class UserFinder(da: DatabaseAccess, sf: SecurityFilter) {
  import Types._

  val particularUser:Id = 1L

  def findUser(): Option[(Id, Name, Surname)] = if(sf.isClassified(particularUser)) None else da.getUserFromDB(particularUser)
}

class UserStatsReader(uf: UserFinder) {
  def readStats: String = {
    uf.findUser() match {
      case Some ((id, name, surname)) => s"Stats for $name $surname: -> .... <stats>"
      case None => s"Sorry, user classified or not found"
    }
  }
}

trait UserModule {
  import com.softwaremill.macwire._

  lazy val da = wire[DatabaseAccess]
  lazy val sf = wire[SecurityFilter]
  lazy val uf = wire[UserFinder]
  lazy val usr = wire[UserStatsReader]
}