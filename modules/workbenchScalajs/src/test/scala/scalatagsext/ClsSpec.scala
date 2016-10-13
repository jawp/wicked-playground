package scalatagsext

import utest._

import scalatags.JsDom.all._

object ClsSpec extends TestSuite {

  val tests = this {


//    These will not work under rhino, but they work in browsers (Chrome, IE, Edge)
//    In test environment element.classList is undefined
//    at least I test if they compile
    'classAttr {
//      import scalatagsext.JsDom.all._
      trait x
      val a = new x {}

      val t = div(cls.:=(a), cls := "anaga")
//      val outerHtml = t.render.outerHTML
//      assert(outerHtml == """<div class="atlas anaga"/>""")
    }

    'classAttrManyArgs {
      implicit val x = binAnyClassAttrValue
      val t = div(cls := ("atlas", "anaga"))
//      val outerHtml = t.render.outerHTML
//      assert(outerHtml == """<div class="atlas anaga"/>""")
    }
  }
}
