package framework

import org.scalajs.dom.{Element, Node}
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

// todo bindFrag and bindFrags
//  implicit class bindRxFrag[T <: Frag](e: Rx[T]) extends Modifier {
//    def applyTo(t: Element) = {
//      val element = new AtomicReference(e().render)
//      t.appendChild(element get())
//      e foreach( { current =>
//        val previous = element getAndSet current.render
//        t.replaceChild(element.get, previous)
//      })
//    }
//  }
//
//  private def comment(txt: String) = new Modifier {
//    override def applyTo(t: Element): Unit = {
//      val c = new Comment
//      c.data = txt
//      t.appendChild(c)
//    }
//  }
//
//  private val emptyComment = new Modifier {
//    override def applyTo(t: Element): Unit = {
//      t.appendChild(new Comment)
//    }
//  }
//
//  private def rxNull() = new Frag {
//    def render: Element = {
//      val elem = dom.document.createElementNS(null, "rx-null")
//      elem.asInstanceOf[Element]
//    }
//    override def applyTo(t: Element): Unit = t.appendChild(render)
//  }
//
//  implicit class bindRxFrags[T <: Frag](rx: Rx[immutable.Iterable[T]]) extends Modifier {
//
//    override def applyTo(t: Element) = {
//      val nonempty = rx.map(t => if (t.isEmpty) List(rxNull()) else t)
//      val elements = new AtomicReference(nonempty().map(_.render))
//      elements get() foreach t.appendChild
//      rx foreach( { current =>
//        val nonempty =  if (current.isEmpty) List(rxNull()) else current
//        val previous = elements getAndSet nonempty.map(_.render)
//        replace(previous, elements.get(), t)
//      })
//    }
//  }
//
//  private def replace(oldies: Iterable[Node], newbies: Iterable[Node], parent: dom.Element): Unit = {
//    val i = parent.childNodes.indexOf(oldies.head)
//    if (i < 0) throw new IllegalStateException("Children changed")
//    oldies foreach parent.removeChild
//
//    if (parent.childNodes.length > i) {
//      val next = parent.childNodes.item(i)
//      newbies foreach (parent.insertBefore(_, next))
//    } else {
//      newbies foreach parent.appendChild
//    }
//  }

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