package howitworks.scalaz


class OrderDemo extends wp.Spec {

  import scalaz.Order
  import scalaz.syntax.all._

  "hello Order" in {

    //here lives Order[Int] which is required in below line
    import scalaz.std.anyVal._

    //this is needed by Order syntax
    implicit val order: Order[Person] = Order.orderBy[Person, Int](_.age)

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
    //Let's say you've changed your mind and now you want to order by another attribute, say star craft high score:
    import scalaz.std.anyVal._
    implicit val order: Order[Person] = Order.orderBy[Person, Long](_.starCraftHighScore)

    (son < mum) mustBe false
    (son <= mum) mustBe false
    (son > mum) mustBe true
    (son >= mum) mustBe true
    // ...
  }

  //model and data
  case class Person(age: Int, starCraftHighScore: Long, name: String)

  lazy val son = Person(15, 2000000L, "Jim")
  lazy val mum = Person(44, 100L, "Ann")
}

