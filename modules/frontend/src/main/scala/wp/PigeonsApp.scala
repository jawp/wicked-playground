package wp

import org.scalajs.dom
import shared.TinyLocator
import wp.textareastats.TextAreaStats

import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js

@JSExport
object PigeonsApp {
  val token = java.util.UUID.randomUUID().toString
  val timeout = 10.minutes.toMillis.toInt

  var savedServerVersion = Option.empty[String]

  def checkRefresh(): Unit = {
    val serverVersionFut = dom.ext.Ajax.post("http://localhost:8080/register",
      timeout = timeout, data = token).map(_.responseText)
    serverVersionFut.onComplete {
      case util.Success(serverVersion)
        if savedServerVersion.exists(_ != serverVersion) =>
        dom.window.location.reload(true)
      case result =>
        savedServerVersion = savedServerVersion.orElse(result.toOption)
        val f: Function0[Any] = checkRefresh
        dom.window.setTimeout(f, 1000)
    }
  }

  @JSExport
  def main(): Unit = {
    println(s"token = $token")
    checkRefresh()
    val mainDiv = dom.document.getElementById(
      TinyLocator.theOnlyElementIdWeNeed)
    mainDiv.innerHTML = ""
    mainDiv.appendChild(TextAreaStats.render)
  }
}
