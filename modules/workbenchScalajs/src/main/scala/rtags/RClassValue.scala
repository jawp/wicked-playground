//package rtags
//
//import org.scalajs.dom
//import org.scalajs.dom.Element
//import rx.Rx.Dynamic
//import rx._
//
//import scalatags.JsDom.all._
//
//
//trait RClassValue {
//
//  import Cls.JsDom._
//
//  implicit def RxDynamicClassValue[T: ClassValue](implicit ctx: Ctx.Owner) = new ClassValue[Var[T]] {
////    override def apply(r: Dynamic[String], v: String*): Unit =  r.trigger {
////      jsDomClassAttrValue.apply(t, r.now)
////    }
//    override def apply(t: Element, v: Var[T]*): Unit = v.foreach(r => r.trigger(
//      implicitly[ClassValue[T]].apply(t, r.now)
//    ))
//  }
//
//  implicit def RxClassValue[T: ClassValue](implicit ctx: Ctx.Owner) = new ClassValue[Rx[T]] {
//    def apply(t: Element, a: Attr, r: Rx[T]): Unit = {
//      r.trigger {
//        implicitly[ClassValue[T]].apply(t, a, r.now)
//      }
//    }
//  }
//
//  implicit def VarClassValue[T: ClassValue](implicit ctx: Ctx.Owner) = new ClassValue[Var[T]] {
//    def apply(t: Element, a: Attr, r: Var[T]): Unit = {
//      r.trigger {
//        implicitly[ClassValue[T]].apply(t, a, r.now)
//      }
//    }
//  }
//
//}
//
//object RClassValue extends RClassValue