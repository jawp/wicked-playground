package wp

import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport

@JSExport
object PigeonsApp {

  @JSExport
  def main(mainDiv: html.Element): Unit = {
    mainDiv.innerHTML = ""
    mainDiv.appendChild(wp.pigeonapp1.MainPresenter.render())
  }
}

