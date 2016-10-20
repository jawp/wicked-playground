package rtags

import org.scalajs.dom.Element
import rx._

import scalatags.JsDom.all._


trait RAttrValue {

  implicit def RxDynamicAttrValue[T: AttrValue](implicit ctx: Ctx.Owner) = new AttrValue[Rx.Dynamic[T]] {
    def apply(t: Element, a: Attr, r: Rx.Dynamic[T]): Unit = {
      r.trigger {
        implicitly[AttrValue[T]].apply(t, a, r.now)
      }
    }
  }

  implicit def RxAttrValue[T: AttrValue](implicit ctx: Ctx.Owner) = new AttrValue[Rx[T]] {
    def apply(t: Element, a: Attr, r: Rx[T]): Unit = {
      r.trigger {
        implicitly[AttrValue[T]].apply(t, a, r.now)
      }
    }
  }

  implicit def VarAttrValue[T: AttrValue](implicit ctx: Ctx.Owner) = new AttrValue[Var[T]] {
    def apply(t: Element, a: Attr, r: Var[T]): Unit = {
      r.trigger {
        implicitly[AttrValue[T]].apply(t, a, r.now)
      }
    }
  }

}

object RAttrValue extends RAttrValue