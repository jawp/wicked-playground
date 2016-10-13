package scalatagsext

import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scalatags.JsDom.GenericAttr

trait LowPriorityImplicits {


  implicit object binJsAnyClassValue extends generic.ClassValue[dom.Element, js.Any] {

    override def apply(t: dom.Element, v: js.Any*): Unit = {
      val maybeClassList: UndefOr[DOMTokenList] = t.classList

      //In some "browsers" (in tests) maybeClassList isundefined.
      //This wont fix the problem
      //
      //if(maybeClassList.isEmpty) {
      //  println("maybeClassList is empty, adding class to it")
      //  createEmptyClasstag(t, Attr("class"), "abc")
      //  println(s"outerHTML is ${t.outerHTML}")
      //}
      //else ()n

      v.foreach { x =>
        t.classList.add(x.toString)
      }
    }

  }

//  implicit def bindAnyLikeClassValue[T](implicit ev: T => js.Any) = new generic.AttrValue[dom.Element, T]{
//    def apply(t: dom.Element, a: generic.Attr, v: T): Unit = {
//      t.asInstanceOf[js.Dynamic].updateDynamic(a.name)(v)
//    }
//  }
//  implicit class bindNode(e: dom.Node) extends generic.Frag[dom.Element, dom.Node] {
//    def applyTo(t: Element) = t.appendChild(e)
//    def render = e
//  }


  private lazy val createEmptyClasstag = new GenericAttr[String]
}