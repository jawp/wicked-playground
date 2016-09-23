package wp

import org.scalatest._
import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatest.prop.PropertyChecks

class   WickedSpec
extends FreeSpec
with    MustMatchers
with    DiagrammedAssertions
with    PropertyChecks
with    TryValues
with    OptionValues
with    AppendedClues
with    FakePrintln
with    ScalaFutures


trait FakePrintln {
  /**
    * This will supress printlining in tests.
    * Comment it out when playing around.
    */
  def println[A](s: A): Unit = FakePrintln.notifyUser
}

object FakePrintln {
  lazy val notifyUser = Console.println("The 'println' was shadowed. See wp.FakePrintln trait")
}

