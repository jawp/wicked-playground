package testrtags

import rtags.Cls.cls
import rtags._
import utest._

import scalatags.JsDom.all._

object ClassAttrSpec extends TestSuite {
  val tests = this {


//    These will not work under rhino, but they work in browsers (Chrome, IE, Edge)
//    In test environment element.classList is undefined
//    at least I test if they compile
    'classAttr {
      import Cls._
      val t = div(cls := "atlas", cls := "anaga")
//      val outerHtml = t.render.outerHTML
//      assert(outerHtml == """<div class="atlas anaga"/>""")
    }

    'classAttrManyArgs {
      val t = div(cls := ("atlas", "anaga"))
//      val outerHtml = t.render.outerHTML
//      assert(outerHtml == """<div class="atlas anaga"/>""")
    }
  }
}
