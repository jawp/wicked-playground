package howitworks.scalaz

import scala.collection.immutable.NumericRange.Inclusive
import scalaz.{EphemeralStream, Ordering}


class EnumDemo extends wp.Spec {

  import scalaz.Enum
  import scalaz.syntax.all._


  "Hello Enum" in {

    //Enum is something more than Order
    //It additionally contains information about successors and ancestors
    //Order tells only which one is greater, Enum can tell what is successor/predecessor of given element

    //define range scala way:
    val range: Inclusive[Char] = 'a' to 'j'

    //The same but scalaz way


    //first import Enum instance for char so the Enum syntax start working
    import scalaz.std.anyVal._

    val rangeScalazWay = 'a' |-> 'd'

    // |-> returns List
    rangeScalazWay mustBe List('a', 'b', 'c', 'd')

    // |=> returns Stream
    val stream: EphemeralStream[Char] = 'a' |=> 'd'

    // --- -+-
    'a' --- (10) mustBe 'W' withClue "the 10th element preceding the 'a'"
    'a' -+- (10) mustBe 'k' withClue "the 10th element after the 'a'"

    //and so on.
    //see EnumOps for more syntax operators
  }

  "custom Enum" in {
    implicit val counterEnum = new Enum[Counter] {
      override def succ(a: Counter): Counter = a.next
      override def pred(a: Counter): Counter = a.prev
      override def order(x: Counter, y: Counter): Ordering = Ordering.fromLessThan(x, y)(_.v < _.v)
    }

    val c = Counter(0, 5)
    c.succ mustBe Counter(1, 5)
    c.succ.succ mustBe Counter(2, 5)

    //this will not compile
    //Counter(0, 5) to Counter(3, 5)

    //but this will
    (Counter(0, 5) |-> Counter(3, 5)) mustBe List(Counter(0,5), Counter(1,5), Counter(2,5), Counter(3,5))
  }

  //model and data
  case class Counter(v: Int, bound: Int) {
    def next: Counter = Counter((v + 1) % bound, bound)
    def prev: Counter = Counter((v + bound - 1) % bound, bound)
  }

}
