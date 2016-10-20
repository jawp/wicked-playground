package wp.pigeonapp1

import org.scalajs.dom.html
import org.scalajs.dom.html.{Div, UList}
import PigeonModel._

import scalatags.JsDom.all._

object MainPresenter {

  def render(): Div = {
    showPigeons(worksheet)
    mainDiv.render
  }

  private lazy val actions = div(
    ul(
      li(span(a("show pigeons", href := "#", onclick := ( () => showPigeons(worksheet))))),
      li(span(a("add pigeon", href := "#"), onclick := ( () => AddPigeonPresenter.showAddPigeonForm(worksheet)))),
      li(span(a("remove head pigeon", href := "#"), onclick := ( () => removeHeadPigeon(worksheet))))
    )
  ).render

  private lazy val worksheet = div().render
  private lazy val mainDiv = div(
    actions,
    worksheet
  )

  private def removeHeadPigeon(target: html.Element) = {
    PigeonModel.removePigeon()
    val pigeonsHtml: UList = render(PigeonModel.pigeons)
    target.innerHTML = ""
    target.appendChild(pigeonsHtml)
  }

  private def render(pigeons: Map[PigeonId, Pigeon]) = ul(pigeons.map(kv => li(kv._2.name)).toSeq :_*).render

  private def showPigeons(target: html.Element) = {
    val pigeonsHtml: UList = render(PigeonModel.pigeons)
    target.innerHTML = ""
    target.appendChild(pigeonsHtml)
  }
}
