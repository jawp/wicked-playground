package wp

import org.scalajs.dom
import org.scalajs.dom.html
import wp.textareastats.TextAreaStats

import scala.scalajs.js.annotation.JSExport



@JSExport
object PigeonsApp {

  @JSExport
  def main(mainDiv: html.Element): Unit = {
    mainDiv.innerHTML = ""
    mainDiv.appendChild(TextAreaStats.render)
  }
}

