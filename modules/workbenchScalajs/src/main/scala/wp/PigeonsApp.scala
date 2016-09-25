package wp

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.{Button, Div, LI, UList}
import org.scalajs.dom.raw.{Element, HTMLElement}
import wp.PigeonModel.{Flying, Pigeon, Standing}
import wp.model.PigeonId

import scala.util.Try
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.stylesheet._

object PigeonModel {

  sealed trait Status
  case object Landing extends Status
  case object Starting extends Status
  case class Flying(action: Action) extends Status
  case class Standing(action: Action) extends Status

  sealed trait Action
  case object Pecking extends Action //dziobać
  case object Shitting extends Action

  case class Pigeon(name: String, age: Int, status: Status)
  type PigeonId = Int

  var pigeons = Map[PigeonId, Pigeon](
    1 -> Pigeon("Lucy", 1, Landing),
    2 -> Pigeon("Bob", 2, Flying(Shitting)),
    3 -> Pigeon("Carmen", 2, Starting),
    4 -> Pigeon("Graham", 2, Standing(Pecking)),
    5 -> Pigeon("All", 2, Landing)
  )

  def addPigeon(pigeon: Pigeon): PigeonId = {
    val id: PigeonId = pigeons.keySet.max + 1
    pigeons  = pigeons + (id -> pigeon)
    id
  }
  def removePigeon(): Unit = {
    pigeons = pigeons.tail
  }
}

object Presenter {
  import PigeonModel._

  def render(pigeons: Map[PigeonId, Pigeon]) = ul(pigeons.map(kv => li(kv._2.name)).toSeq :_*).render

  def showPigeons(target: html.Element) = {
    val pigeonsHtml: UList = render(PigeonModel.pigeons)
    target.innerHTML = ""
    target.appendChild(pigeonsHtml)
  }

  def removeHeadPigeon(target: html.Element) = {
    PigeonModel.removePigeon()
    val pigeonsHtml: UList = render(PigeonModel.pigeons)
    target.innerHTML = ""
    target.appendChild(pigeonsHtml)
  }

}

object AddPigeonPresenter {

  def showAddPigeonForm(target: html.Element) = {
    target.innerHTML = ""
    target.appendChild(addPigeonForm(target))
  }

  def showPigeonAdded(id: PigeonId, target: html.Element) = {
    target.innerHTML = ""
    target.appendChild(pigeonAdded(id))
  }

  private def verifyName(name: String): Option[String] = {
    if(name == null || name.isEmpty) Some("Name must be not empty")
    else if(name.length <= 2 ) Some("Name must have at least 3 letters")
    else if(name.length > 10 ) Some("Name must have at most 9 letters")
    else None
  }

  private def verifyAge(age: String): Option[String] = {
    if(age == null || age.isEmpty) Some("Age must be not empty")
    else if(Try(age.toInt).isFailure) Some("Age must be valid number")
    else if(age.toInt < 0 ) Some("Age must be positive")
    else if(age.toInt > 5 ) Some("Pigeons live at most 5 years")
    else None
  }

  private def addPigeon0(name: String, age: Int): PigeonId = {
    val pigeon = Pigeon(
      name = name,
      age = age,
      status = PigeonModel.Landing
    )
    PigeonModel.addPigeon(pigeon)
  }

  def addPigeon(name: String, age: String, target: html.Element): Unit = {
    val nameErrors: Option[String] = verifyName(name)
    val ageErrors: Option[String] = verifyAge(age)
    nameErrors.fold(nameLabel.innerHTML = "")(err => nameLabel.innerHTML = err)
    ageErrors.fold(ageLabel.innerHTML = "")(err => ageLabel.innerHTML = err)
    if(nameErrors.isEmpty && ageErrors.isEmpty) {
      val id = addPigeon0(name, age.toInt)
      showPigeonAdded(id, target)
    }
  }


  val nameLabel = label().render
  val nameInput = input(`type` := "text", placeholder := "name").render
  val ageLabel = label().render
  val ageInput = input(`type` := "text", placeholder := "age").render

  val addPigeonPanel = div().render

  def addPigeonForm(target: html.Element) = div(
    span(nameInput, nameLabel),
    span(ageInput, ageLabel),
    button(onclick := (() => addPigeon(nameInput.value, ageInput.value, target)), "add pigeon")
    //TODO state
  ).render

  def pigeonAdded(id: PigeonId) = p(
    s"Thank you. Pigeon added (id = ${id})."
  ).render

}

object App {

  private val actions = div(
    ul(
      li(span(a("show pigeons", href := "#", onclick := ( () => Presenter.showPigeons(worksheet))))),
      li(span(a("add pigeon", href := "#"), onclick := ( () => AddPigeonPresenter.showAddPigeonForm(worksheet)))),
      li(span(a("remove head pigeon", href := "#"), onclick := ( () => Presenter.removeHeadPigeon(worksheet))))
    )
  ).render

  private val worksheet = div(
    "todo ..."
  ).render

  lazy val app = div(
    actions,
    worksheet
  )

  def render() = {
    Presenter.showPigeons(worksheet)
    app.render
  }
}

object AppStyles extends StyleSheet {


  override def defaultSheetName: String = ???

  override def allClasses: Seq[Cls] = ???
}

@JSExport
object PigeonsApp {

  @JSExport
  def main(mainDiv: html.Element): Unit = {

    val pigeonDiv: html.Div = div().render
    mainDiv.innerHTML = ""
    mainDiv.appendChild(pigeonDiv)

    val killPigeonButton: html.Button = button(
      onclick := { () =>
        PigeonModel.removePigeon()
        renderPigeons(pigeonDiv)
      },
      "kill pigeon",
      id := "killPigeonButton"
    ).render

    renderPigeons(pigeonDiv)

    mainDiv.appendChild(killPigeonButton)

    mainDiv.innerHTML = ""
    mainDiv.appendChild(App.render)

  }

  def renderPigeons(pigeonDiv: html.Div) = {
    dom.console.log("rendering")
    val pigeonsList: UList = ul(PigeonModel.pigeons.map { case (id, pigeon) =>
      li(pigeon.name)
    }.toSeq: _*).render

    pigeonDiv.innerHTML = ""
    pigeonDiv.appendChild(pigeonsList)
    pigeonDiv.appendChild(pigeonsList)
  }

  val animatinStyles = MainCss.allClasses
  var styleIndex = 0


  def blink(target: dom.raw.HTMLElement): Unit = {
    styleIndex = (styleIndex + 1) % animatinStyles.size
    val style: Cls = animatinStyles(styleIndex)
    animatinStyles.foreach(x => target.classList.remove(x.name))
    target.classList.add(style.name)
    dom.console.log(s"changed style to $style")
  }
}

object MainCss extends StyleSheet {
  val style1 = cls(
    backgroundColor := "blueviolet"
  )
  val style2: Cls = cls(
    backgroundColor := "aquamarine"
  )

  override def defaultSheetName: String = "MainCss"

  override def allClasses: Seq[Cls] = Seq(style1, style2)
}