package wp.pigeonapp1

import org.scalajs.dom.html
import PigeonModel._

import scala.util.Try
import scalatags.JsDom.all._

object AddPigeonPresenter {

  def showAddPigeonForm(target: html.Element) = {
    target.innerHTML = ""
    target.appendChild(addPigeonForm(target))
  }

  private def showPigeonAdded(id: PigeonId, target: html.Element) = {
    target.innerHTML = ""
    target.appendChild(pigeonAdded(id))
  }

  private def verifyName(name: String): Option[String] = {
    if (name == null || name.isEmpty) Some("Name must be not empty")
    else if (name.length <= 2) Some("Name must have at least 3 letters")
    else if (name.length > 10) Some("Name must have at most 9 letters")
    else None
  }

  private def verifyAge(age: String): Option[String] = {
    if (age == null || age.isEmpty) Some("Age must be not empty")
    else if (Try(age.toInt).isFailure) Some("Age must be valid number")
    else if (age.toInt < 0) Some("Age must be positive")
    else if (age.toInt > 5) Some("Pigeons live at most 5 years")
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

  private def addPigeon(name: String, age: String, target: html.Element): Unit = {
    val nameErrors: Option[String] = verifyName(name)
    val ageErrors: Option[String] = verifyAge(age)
    nameErrors.fold(nameLabel.innerHTML = "")(err => nameLabel.innerHTML = err)
    ageErrors.fold(ageLabel.innerHTML = "")(err => ageLabel.innerHTML = err)
    if (nameErrors.isEmpty && ageErrors.isEmpty) {
      val id = addPigeon0(name, age.toInt)
      showPigeonAdded(id, target)
    }
  }

  private lazy val nameLabel = label().render
  private lazy val nameInput = input(`type` := "text", placeholder := "name").render
  private lazy val ageLabel = label().render
  private lazy val ageInput = input(`type` := "text", placeholder := "age").render

  private lazy val addPigeonPanel = div().render

  private def addPigeonForm(target: html.Element) = div(
    span(nameInput, nameLabel),
    span(ageInput, ageLabel),
    button(onclick := (() => addPigeon(nameInput.value, ageInput.value, target)), "add pigeon")
    //TODO state
  ).render

  private def pigeonAdded(id: PigeonId) = p(
    s"Thank you. Pigeon added (id = ${id})."
  ).render

}

