package howitworks.scalaz


class OrderDemo extends wp.Spec {

  import scalaz.Order
  import scalaz.syntax.all._


  "hello Order" in {

    import OrderByAge._
    // <- this brings Order[Person] implicit in scope, so below syntax is enabled

    (son < mum) mustBe true
    (son <= mum) mustBe true
    (son > mum) mustBe false
    (son >= mum) mustBe false

    (son lt mum) mustBe true
    (son lte mum) mustBe true
    (son gt mum) mustBe false
    (son gte mum) mustBe false

    (son max mum) mustBe mum
    (son min mum) mustBe son

    (son cmp mum) mustBe scalaz.Ordering.LT
    (mum cmp son) mustBe scalaz.Ordering.GT

    (son ?|? mum) mustBe scalaz.Ordering.LT
    (mum ?|? son) mustBe scalaz.Ordering.GT

    //Order extends Equal !

    (son ≟ mum) mustBe false
    (son ≠ mum) mustBe true
  }

  "order by high score" in {
    //Let's say you've changed your mind and now you want to order by another attribute, say starcraft high score:

    import OrderByHighScore._

    // <- this will shadow previous implicit and brings new Order[Person] in scope

    (son < mum) mustBe false
    (son <= mum) mustBe false
    (son > mum) mustBe true
    (son >= mum) mustBe true
    // ...
  }


  object OrderByAge {

    import scalaz.std.anyVal._

    // <-- here lives Order[Int] which is required in below line
    implicit val order: Order[Person] = Order.orderBy[Person, Int](_.age)
  }

  object OrderByHighScore {

    import scalaz.std.anyVal._

    // <-- here lives Order[Long] which is required in below line
    implicit val order: Order[Person] = Order.orderBy[Person, Long](_.starCraftHighScore)
  }

  //model and data
  case class Person(age: Int, starCraftHighScore: Long, name: String)

  lazy val son = Person(15, 2000000L, "Jim")
  lazy val mum = Person(44, 100L, "Ann")
}

