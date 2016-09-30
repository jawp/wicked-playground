package rtags


import java.util.Objects._

import org.scalajs.dom
import org.scalajs.dom.DOMTokenList

import scala.scalajs.js.UndefOr
import scalatags.JsDom.GenericAttr

object Cls {

  object cls {

    def :=[Builder](v: String*)(implicit ev: ClassValue[Builder]) = {
      requireNonNull(v)
      ClassModifier[Builder](v, ev)
    }

    def empty[Builder](implicit ev: ClassValue[Builder]) = this := ()
  }

  case class ClassModifier[Builder](v: Seq[String], ev: ClassValue[Builder]) extends scalatags.generic.Modifier[Builder] {
    override def applyTo(t: Builder): Unit = {
      ev.apply(t, v: _*)
    }
  }

  trait ClassValue[Builder] {
    def apply(t: Builder, v: String*)
  }


  object ClassValue {

    private val createEmptyClasstag = new GenericAttr[String]

    implicit val jsDomClassAttrValue = new ClassValue[dom.Element] {
      override def apply(t: dom.Element, v: String*): Unit = {
        val maybeClassList: UndefOr[DOMTokenList] = t.classList

        //In some "browsers" (in tests) maybeClassList isundefined.
        //This wont fix the problem
        //
        //if(maybeClassList.isEmpty) {
        //  println("maybeClassList is empty, adding class to it")
        //  createEmptyClasstag(t, Attr("class"), "abc")
        //  println(s"outerHTML is ${t.outerHTML}")
        //}
        //else ()

        v.foreach{ x =>
          t.classList.add(x)
        }
      }
    }

  }
}