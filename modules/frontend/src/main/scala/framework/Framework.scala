package framework

import org.scalajs.dom.Element
import org.scalajs.dom.Node
import org.scalajs.dom.raw.HTMLElement
import rx._

import scala.annotation.tailrec
import scala.util.{Failure, Success}
import scalatags.JsDom.all._

/**
  * based on https://github.com/lihaoyi/workbench-example-app/blob/todomvc/src/main/scala/example/Framework.scala
  * A minimal binding between Scala.Rx and Scalatags and Scala-Js-Dom
  */
trait Framework {
  import Framework._
  /**
    * Sticks some Rx into a Scalatags fragment, which means hooking up an Obs
    * to propagate changes into the DOM.
    */
  implicit def RxFrag[T](rxt: Rx[T])(implicit f: T => Frag, ctx: Ctx.Owner): Frag = {

    def now(): Node = rxt match {
      case r: Rx.Dynamic[T] => r.toTry match {
        case Success(v) => v.render
        case Failure(e) => span(e.getMessage, backgroundColor := "red").render
      }
      case v: Var[T] => v.now.render
    }

    val container = span(Framework.`scala-rx`)(now()).render

    rxt.triggerLater {
      //Rx[Seq[T]] can generate multiple children per propagate, so use clearChildren instead of replaceChild
      replaceChildrenWith(container, now())
    }

    bindNode(container)
  }

  implicit def RxAttrValue[T: AttrValue](implicit ctx: Ctx.Owner) = new AttrValue[Rx.Dynamic[T]]{
    def apply(t: Element, a: Attr, r: Rx.Dynamic[T]): Unit = {
      r.trigger { implicitly[AttrValue[T]].apply(t, a, r.now) }
    }
  }

  implicit def RxStyleValue[T: StyleValue](implicit ctx: Ctx.Owner) = new StyleValue[Rx.Dynamic[T]]{
    def apply(t: Element, s: Style, r: Rx.Dynamic[T]): Unit = {
      r.trigger { implicitly[StyleValue[T]].apply(t, s, r.now) }
    }
  }
}

object Framework extends Framework {
  val `scala-rx` = attr("scala-rx") := ""

  @tailrec def clearChildren(node: Node): Unit = {
    if(node.firstChild != null) {
      node.removeChild(node.firstChild)
      clearChildren(node)
    }
  }

  def replaceChildrenWith(parent: Node, newChild: Node): Unit = {
    clearChildren(parent)
    parent.appendChild(newChild)
  }
}