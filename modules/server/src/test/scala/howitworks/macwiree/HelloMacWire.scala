package howitworks.macwiree

import java.util.concurrent.atomic.AtomicInteger

import com.softwaremill.macwire._
import com.softwaremill.tagging._

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

  "many module instances - @Module" in {
    val m1 = wire[UModule]
    val m2 = wire[UModule]
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
  "wire set" in {
    wire[UModule].figure.wheels.map(_.radius).toList mustBe List(11, 22, 33)
  }

  "tagging" in {
    wire[UModule].wrappedInt.i mustBe 0
  }

  "many wirings int the module" in {
    val m = wire[UModule]

  }

  "combining modules" in {
      //below must be provided before wiring ChildModule
    lazy val mum = wire[MumModule]
    lazy val dad = wire[DadModule]

    lazy val m = wire[ChildModule]

    m.douther
    m.son
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

  def findUser(): Option[(Id, Surname, Surname)] = if(sf.isClassified(particularUser)) None else da.getUserFromDB(particularUser)
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

case class Wheel(radius: Double)

class Figure(val wheels: Set[Wheel])

trait UserModule {
  import Types._

  lazy val da = wire[DatabaseAccess]
  lazy val classified: List[Id] = List(2)
  lazy val sf = wire[SecurityFilter]
  lazy val uf = wire[UserFinder]
  lazy val usr = wire[UserStatsReader]
  def nextCounter = wire[Counter]
  val globalCounter = wire[Counter]

  lazy val w1 = new Wheel(11)
  lazy val w2 = new Wheel(22)
  lazy val w3 = new Wheel(33)
  lazy val wheels = wireSet[Wheel]
  lazy val figure = wire[Figure]

  lazy val i0 = 0.taggedWith[I0]
  lazy val i1 = 1

  val wrappedInt = wire[WrappedInt]

  lazy val d0 = 0.1

  lazy val doubleVar0 = wire[DoubleVar]
  lazy val doubleVar1 = wire[DoubleVar]
}

class DoubleVar(var d: Double)

trait I0
class WrappedInt(val i: Int @@ I0)

@Module
class UModule extends UserModule


//
object Family {
  trait Food
  trait Surname
  trait Color
}
import Family._

class Douther(surname: String @@ Surname, eyesColor: Int @@ Color, food: Int @@ Food)
class Son(surname: String @@ Surname, food: Int @@ Food)

@Module
class MumModule {
  lazy val food = 123.taggedWith[Food]
  lazy val eyesColor = 334476.taggedWith[Color]
}

@Module
class DadModule {
  lazy val surname = "Kowalsky".taggedWith[Surname]
}

@Module
class ChildModule(mum: MumModule, dad: DadModule) {
  lazy val douther = wire[Douther]
  lazy val son = wire[Son]
}
