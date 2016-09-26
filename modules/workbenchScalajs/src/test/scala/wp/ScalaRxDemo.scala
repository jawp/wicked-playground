package wp

import rx._
import rx.Ctx.Owner

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

}