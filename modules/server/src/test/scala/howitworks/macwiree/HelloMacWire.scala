package howitworks.macwiree

import java.util.concurrent.atomic.AtomicInteger

import com.softwaremill.macwire._

class HelloMacWire extends wp.Spec {

  "let it wire" in {
    val module = new UserModule {}
    module.usr.readStats mustBe "(1) Stats for Sarah Kerrigan: -> .... <stats>"
  }

  "let it override wires" in {
    val module = new UserModule {
      import Types._
      override lazy val da: DatabaseAccess = new DatabaseAccess {
        override def getUserFromDB(id: Id): Option[(Id, Name, Surname)] = Some((1L, "Infested", "Kerrigan"))
      }
    }
    module.usr.readStats mustBe "(1) Stats for Infested Kerrigan: -> .... <stats>"
    module.usr.readStats mustBe "(2) Stats for Infested Kerrigan: -> .... <stats>"
  }

  "many module instances" in {
    val m1 = new UserModule {}
    val m2 = new UserModule {}
    m1.usr.readStats mustBe "(1) Stats for Sarah Kerrigan: -> .... <stats>"
    m1.usr.readStats mustBe "(2) Stats for Sarah Kerrigan: -> .... <stats>"
    m1.usr.readStats mustBe "(3) Stats for Sarah Kerrigan: -> .... <stats>"

    m2.usr.readStats mustBe "(1) Stats for Sarah Kerrigan: -> .... <stats>"
    // So m1 and m2 have their own instances ...
  }

  "@Module" in {
    //UModule must be a class, it's not allowed to pass trait here
    val module = wire[UModule]
    module.usr.readStats mustBe "(1) Stats for Sarah Kerrigan: -> .... <stats>"
  }

  "singleton vs prototype" in {
    val module = wire[UModule]
    val c1 = module.nextCounter
    val c2 = module.nextCounter
    val c3 = module.globalCounter
    val c4 = module.globalCounter

    c1.next() mustBe 1
    c1.next() mustBe 2
    c1.next() mustBe 3

    c2.next() mustBe 1
    c2.next() mustBe 2
    c2.next() mustBe 3

    c3.next() mustBe 1
    c3.next() mustBe 2
    c3.next() mustBe 3
    c4.next() mustBe 4
    c4.next() mustBe 5
    c4.next() mustBe 6

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

class SecurityFilter(classified: List[Types.Id]) {
  def isClassified(id: Types.Id): Boolean = classified.contains(id)
}

class UserFinder(da: DatabaseAccess, sf: SecurityFilter) {
  import Types._

  val particularUser:Id = 1L

  def findUser(): Option[(Id, Name, Surname)] = if(sf.isClassified(particularUser)) None else da.getUserFromDB(particularUser)
}

class UserStatsReader(uf: UserFinder) {
  var counter: Int = 0
  def readStats: String = {
    uf.findUser() match {
      case Some ((id, name, surname)) =>
        counter = counter + 1
        s"($counter) Stats for $name $surname: -> .... <stats>"
      case None =>
        counter = counter + 1
        s"Sorry, user classified or not found"
    }
  }
}

class Counter {
  val c = new AtomicInteger(1)
  def next(): Int = c.getAndIncrement()
}

trait UserModule {
  import Types._

  lazy val da = wire[DatabaseAccess]
  lazy val classified: List[Id] = List(2)
  lazy val sf = wire[SecurityFilter]
  lazy val uf = wire[UserFinder]
  lazy val usr = wire[UserStatsReader]
  def nextCounter = wire[Counter]
  val globalCounter = wire[Counter]
}

@Module
class UModule extends UserModule