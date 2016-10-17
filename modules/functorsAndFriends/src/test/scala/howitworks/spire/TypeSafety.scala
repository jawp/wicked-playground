package howitworks.spire


import scalaz.syntax.id._

class TypeSafety extends wp.Spec {


  "no strong typing" in {

    //old way, no type safe

    def lendMoney(userId: Int, amount: Int, fee: Int = 200): String = {
      val fee = 200

      s"transferring ${amount} USD to user $userId. He will bring back ${amount + fee}"
    }

    val userId = 123
    val amount = 500 //USD

    lendMoney(amount, userId) // oooooops...
  }

  "strong typing but boiler plated (un)wrapping" in {

    //we can work around it wrapping all things into dedicated case classes
    //It's king of strong typing
    case class MoneyAmount(value: Int)
    case class UserId(value: Int)

    def lendMoney(id: UserId, amount: MoneyAmount, fee: MoneyAmount = MoneyAmount(200)): String = {
      //ehh ... this unwrapping and wrapping makes man crazy
      val returnAmount = MoneyAmount(amount.value + fee.value)
      s"transferring ${amount} USD to user $id. He will bring back $returnAmount"
    }

    val userId = UserId(123)
    val amount = MoneyAmount(500)

    lendMoney(userId, amount)
    //lendMoney2(amount, userId) here compiler rescues
  }

  "strong typing - safe and convenient using Ring type class" in {
    //How to ease the (un)wrapping logic which is present in 'ehh' section?
    case class UserId(value: Int)
    case class MoneyAmount(value: Int)

    import spire.algebra.Ring
    import spire.implicits._

    //not sure if such routing has been already provided by spire
    def makeRing[A, B: Ring](f: A => B)(g: B => A): Ring[A] = new Ring[A] {
      override def one: A = Ring[B].one |> g
      override def plus(x: A, y: A): A = (f(x) + f(y)) |> g
      override def times(x: A, y: A): A = (f(x) * f(y)) |> g
      override def negate(x: A): A = -f(x) |> g
      override def zero: A = Ring[B].zero |> g
    }

    object MoneyAmount {
      implicit val ring = makeRing[MoneyAmount, Int](_.value)(MoneyAmount.apply)
    }

    def lendMoney(id: UserId, amount: MoneyAmount, fee: MoneyAmount = MoneyAmount(200)): String = {
      //Now we're talking :)
      //It is safe and convenient
      val returnAmount = amount + fee
      s"transferring $amount USD to user $id. He will bring back $returnAmount"
    }
    lendMoney(UserId(123), MoneyAmount(400)) mustBe "transferring MoneyAmount(400) USD to user UserId(123). He will bring back MoneyAmount(600)"
  }

  //TODO do type annotations make any sense here?
}
