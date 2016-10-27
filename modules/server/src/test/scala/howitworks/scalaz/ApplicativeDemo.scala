package howitworks.scalaz

import scalaz.syntax.Syntaxes

class ApplicativeDemo extends wp.Spec {

  "Hello Applicative" in {
    import scalaz._
    import Scalaz._

    //take one value inside context, take a function inside a context and apply it to value

    Applicative[Option].ap(3.some)({3 * (_: Int)}.some) mustBe 9.some

    //the same as above but fancy syntax
    3.some <*> {3 * (_: Int)}.some mustBe 9.some

    //when function takes 2 params and you have two args in context use ap2
    Applicative[Option].ap2(3.some, 5.some)({(_:Int)+(_:Int)}.some) mustBe (3+5).some

    //when function takes 3 params and you have two args in context use ap3
    Applicative[Option].ap3(3.some, 5.some, 1.some)({(_:Int)+(_:Int)+(_:Int)}.some) mustBe (3+5+1).some
    //ap4, ap5 ... etc

    // *> discards left arg and returns right
    (3.some *> 5.some) mustBe 5.some

    // but all must be some, in one is none than result is none
    (none *> 5.some) mustBe none


    // <* discards right arg and returns left
    (3.some <* 5.some) mustBe 3.some

    // but all must be some, in one is none than result is none
    (3.some <* none) mustBe none

    //if function is not in context or we just want to combine several values in context using some function
    // we can use ^ ^^ ^^^ ^^^^ ...

    //well, intellij shows red (10.2016) but it wors
    ^(4.some, 6.some)(_ * _ ) mustBe 24.some
    ^^(4.some, 6.some, 8.some)(_ * _ / _) mustBe 3.some
    ^^(none[Int], 6.some, 8.some)(_ * _ / _) mustBe none

    //note that in above Option[A] has one type parameer (Option is kind of *->* )

    //^(4.success[String], 65.success[String])(_+_) this will not compile because Valildation[A,B] is not kind of *->*
    //for validations we need applicative builder |@|

    //intellij is red here (10.2016)
    (4.success[String] |@| 65.success[String])(_+_) mustBe 69.success[String]

    //for options this style will work as well
    (4.some |@| 65.some)(_+_) mustBe 69.some

    //summary
    val a = 1.some
    val b = 2.some
    val c = 3.some
    def f(a: Int, b: Int): Int = a+b

    //below are different ways of doing the same thing:
    ^(a,b)(f) mustBe c
    (a |@| b)(f) mustBe c
    Applicative[Option].ap2(a, b) (Applicative[Option].point(f _)) mustBe c
    //the third way is the most ugly, this is why ^ and |@| take place
  }
}
