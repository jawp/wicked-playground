package rtags

import org.scalajs.dom.Element
import rx._

import scalatags.JsDom.all._


trait RStyleValue {

  implicit def RxDynamicStyleValue[T: StyleValue](implicit ctx: Ctx.Owner) = new StyleValue[Rx.Dynamic[T]] {
    def apply(t: Element, s: Style, r: Rx.Dynamic[T]): Unit = r.trigger(implicitly[StyleValue[T]].apply(t, s, r.now))

  }
  implicit def RxStyleValue[T: StyleValue](implicit ctx: Ctx.Owner) = new StyleValue[Rx[T]] {
    def apply(t: Element, s: Style, r: Rx[T]): Unit = r.trigger(implicitly[StyleValue[T]].apply(t, s, r.now))
  }

  implicit def VarStyleValue[T: StyleValue](implicit ctx: Ctx.Owner) = new StyleValue[Var[T]] {
    def apply(t: Element, s: Style, r: Var[T]): Unit = r.trigger(implicitly[StyleValue[T]].apply(t, s, r.now))
  }
}

object RStyleValue extends RStyleValue
