package wp

import org.scalajs.dom.html.Div
import utest._

import scalatags.JsDom.all._

object RTagsSpec extends TestSuite {

  val tests = this {
    'scalatagDivMustRenderInDom {
      val d: Div = div(
        cls := "sialala",
        id := "theDiv",
        "this is the div"
      ).render

      assert(
        d.outerHTML == """<div class="sialala" id="theDiv">this is the div</div>"""
      )
    }

  }
}
