package wp

import rx._
import rx.Ctx.Owner
import rx.Rx.Dynamic

import scala.collection.mutable
import scala.util.{Failure, Success}

class ScalaRxDemo extends WickedSpec {

  "rx and Ctx.Owner macro magic" in {

    val a = Var(1)
    val b = Var(2)

    //There must be implicit rx.Ctx.Owner in scope
    implicit val ctx = Ctx.Owner.Unsafe  //this wokks here
    //implicit def ctx: Owner = Ctx.Owner.safe() //but this one will not work, wtf ?!

    val c = Rx {
      a() + b() -> Rx {a() - b()}
    }

    trait RxTrait {
      implicit def ctx: Ctx.Owner
    }

    new RxTrait {
      //WFT cont. although here Ctx.Owner.safe() compiles
      override implicit def ctx: Owner = Ctx.Owner.safe()
      val a = Var(1)
      val b = Var(2)
      val c = Rx {
        a() + b()
      }
    }
  }

  "more examples on Ctx.Owner" in {

    import Ctx.Owner.Unsafe._

    var count = 0
    val a = Var(1);
    val b = Var(2)

    def mkRx(i: Int)(implicit ctx: Ctx.Owner) = Rx { count += 1; i + b() }

    def mkC(implicit ctx: Ctx.Owner) = Rx {
      val newRx = mkRx(a())
      newRx()
    }

    val c = mkC

    println(c.now,count) // (3,1)
    a() = 4
    println(c.now,count) // (6,2)
    b() = 3
    println(c.now,count) // (7,4)
    (0 to 100).foreach { i => a() = i }
    println(c.now,count) //(103,105)
    b() = 4
    println(c.now,count) //(104,107)


    def updateA (f: Int => Int)(implicit ctx: Ctx.Owner): Unit = {
      a() = f(a.now)
    }
  }

  import mutable.{MutableList => mList}
  import mutable.{Map => mMap}

  "simple Rx" in {
    import Ctx.Owner.Unsafe._
    val log = mList[Symbol]()
    val empty = mList[Symbol]()
    val x = Var{1}
    x.triggerLater(
      log.+=('xChanged)
    )
    log mustBe empty
    x.now mustBe 1
    x() = 12
    log mustBe mList('xChanged)
    x.now mustBe 12
  }

  "higher ordered rxes" in {
    import Ctx.Owner.Unsafe._
    val log = mList[Symbol]()
    val empty = mList[Symbol]()
    val xA = Var(1)
    xA.triggerLater(
      log.+=('xAChanged)
    )
    val xB = Var(2)
    xB.triggerLater(
      log.+=('xBChanged)
    )
    val xs = Var {
      List(
        xA,
        xB
      )
    }
    xs.triggerLater(
      log.+=('xsChanged)
    )
    log mustBe empty
    xA() = 10
    xA.now mustBe 10
    log mustBe mList('xAChanged)  //outer RX is not fired
    xs.now.map(_.now) mustBe mList(10,2) //but inner Var have been changed
  }

  "higher ordered rxes - Rx instead of Var" in {
    import Ctx.Owner.Unsafe._
    val log = mList[Symbol]()
    val empty = mList[Symbol]()
    val xA = Var(1)
    xA.triggerLater(
      log.+=('xAChanged)
    )
    val xB = Var(2)
    xB.triggerLater(
      log.+=('xBChanged)
    )
    val xs = Var {
      List(
        Rx{xA()+1},
        xB
      )
    }
    xs.triggerLater(
      log.+=('xsChanged)
    )
    log mustBe empty
    xA() = 10
    xA.now mustBe 10
    log mustBe mList('xAChanged)  //outer RX is not fired
    xs.now.map(_.now) mustBe List(11,2) //but inner Var have been changed
    xs() = xs.now :+ xA.map(_ + 10)
    log mustBe mList('xAChanged, 'xsChanged)
    xs.now.map(_.now) mustBe List(11,2,20)
  }

  "Rx is still leaking" in {

    class Test(implicit ctx: Ctx.Owner) {
      var count = 0
      val a = Var(1); val b = Var(2)

      def mkRx(i: Int) = Rx { count += 1; i + b() }
      val c = Rx {
        val newRx = mkRx(a())
        newRx()
      }
      println(c.now, count)
      a() = 4
      println(c.now, count)
      b() = 3
      println(c.now, count) //(7,5) -- 5??

      (0 to 100).foreach { i => a() = i }
      println(c.now, count)
      b() = 4
      println(c.now, count)
      (c.now, count) mustBe (104,211) withClue "still leaky rx -- 211!!"
    }

    import rx.Ctx.Owner.Unsafe._
    new Test()

  }

}