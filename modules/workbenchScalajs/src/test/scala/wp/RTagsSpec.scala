package wp

import rtags._
import rx._
import utest._

import scalatags.JsDom.all._


object RTagsSpec extends TestSuite {

  val tests = this {
    'rAttrValue {

      val rWidth: Var[Int] = Var(10)
      val rHeight: Rx[String] = rWidth.map(_ + 10 + "px")
      val rName: Rx.Dynamic[String] = rWidth.map(x => s"div_$x")

      val t = div(name := rName, heightA := rHeight, widthA:= rWidth).render

      var outerHtml: String = t.outerHTML
      assert(
        outerHtml == """<div name="div_10" height="20px" width="10"/>"""
      )

      rWidth() = 15; outerHtml = t.outerHTML
      assert(
        outerHtml == """<div name="div_15" height="25px" width="15"/>"""
      )
    }

  }
}
