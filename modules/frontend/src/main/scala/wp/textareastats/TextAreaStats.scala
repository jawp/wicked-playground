package wp.textareastats


import org.scalajs.dom

import scalatags.JsDom
import scalatags.JsDom.TypedTag
import scalatags.generic.StylePair
import org.scalajs._
import org.scalajs.dom.html.Pre
import rx.Rx.Dynamic
import rx._

object TextAreaStats {

  import framework.Framework._
  import rx._

  import scalatags.JsDom.all._

  lazy val render: dom.Element = {
    Model.words.map(x => dom.console.log(s"words changed: $x"))
    View.mainDiv
  }

  private object Model {
    val words = Var("") //let's create reactive variable. This will be our mode
    val charactersCount: Rx[Int] = words.map(_.length)
    val wordsCount: Rx[Int] = words.map(_.split(' ').count(_.nonEmpty))
  }

  private object View {
    lazy val wordbox: dom.html.TextArea = {
      val ta = textarea().render //textArea must be rendered first in order to use dom._ API
      ta.onkeyup =
        (x: Any) => {
          dom.console.log("wordbox on keyup ...")
          Model.words() = ta.value //note here we're replacing default onkeyup callback
        }
      ta
    }

    lazy val chars: Rx[Int] = Model.charactersCount
    lazy val words = pre(Model.wordsCount.map(_.toString()))

    class Color(jsName: String, cssName: String) extends Style(jsName, cssName)

    object Color extends Color("color", "color"){
      lazy val A = this := "#382865"
      lazy val B = this := "#FF2865"
      lazy val C: StylePair[dom.Element, String] = this := "#38CC65"
      val colors = List("red", "green", "blue", "cyan", "brown")

      val D: StylePair[dom.Element, Dynamic[String]] = this := Rx {
        val index = chars() % colors.length
        colors(index)
      }
    }

    //    def iPre[T](s: T)(implicit ev$1: T => Frag): TypedTag[Pre] = pre(s, style := "display: inline")
    def iPre[T](s: T)(implicit ev$1: T => Frag): TypedTag[Pre] = pre(s, display.inline, boxShadow := "0 1px 1px 0")

    lazy val mainDiv = div(
      wordbox,
      ul(
        li(span("characters:", raw("&nbsp;"), iPre(chars).apply(color := chars.map(x => if(x == 0) (color.red).v else "inherit")))),
        li(span("characters:", raw("&nbsp;"), iPre(chars).apply(Color.A))),
        li(span("characters:", raw("&nbsp;"), iPre(chars).apply(Color.D))),
        li(span("words:", words)),
        li(div(
          SimpleSS.x,
          "The div"
        ))
      ),
      JsDom.tags2.style(SimpleSS.styleSheetText)

    ).render


  }

}
