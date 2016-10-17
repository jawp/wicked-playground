package wp

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
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
with    TypeTesting


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

trait TypeTesting {

  def theSameType[A,B](implicit ev: A =:= B) = ()
  /***
    * Witness that A and B have the same parent type of SuperType
    */
  def theSameParentType[A <: SuperType, B <: SuperType, SuperType] = ()
  def subType[A, B](implicit ev: A <:< B) = ()
}