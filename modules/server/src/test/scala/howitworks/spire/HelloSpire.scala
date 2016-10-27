package howitworks.spire

import spire.algebra.Field
import spire.algebra.Ring
import spire.implicits._
import spire.math._

class HelloSpire extends wp.Spec {

  "Hello spire!" - {

    "Embarassing floats" in {
      assert(0.2 + 0.1 != 0.3)
    }

    "intro " in {
      def double[A: Ring](x: A): A = x + x
      def triple[A: Ring](x: A): A = x * 3
      (double(3), triple(4))
      r"1/20" mustBe Rational(1, 20)
    }

    "mean" in {
      def mean[T: Field](fs: T*) = fs.foldLeft(Field[T].zero)(_ + _) / fs.size
      mean(1D, 2D, 3D, 4D, 5D) mustBe 3D
      mean(1F, 2F, 3F, 4F, 5F) mustBe 3F
      mean(r"1/2", r"2/3", r"4/5") mustBe r"59/90"
    }

    "creal" in {
      def add(ts: Rational*) = ts.foldLeft(Rational.zero)(_ + _)
      val x: Rational = Real("0.1").toRational(10)
      val y: Rational = Real("0.2").toRational(10)
      val z: Rational = Real("0.3").toRational(10)

      x + y mustBe z
      //      but
      0.2 + 0.1 must not be 0.3

      Rational(1, 2) + Rational(1, 3) mustBe Rational(5, 6)
      Rational(1, 2) + Rational(1, 3) mustBe Rational(10, 12)
    }

    "gdc" in {
      val x = spire.math.gcd(BigInt(1), BigInt(2))
    }

    "float vs spire" in {
      val largeInt: Int = 100 * 1000 * 1000
      val smallFloat: Float = 1.0f / (largeInt: Float)
      (1f + smallFloat) mustBe 1f
      //Rational for rescue
      Rational(1, 1) + Rational(1, largeInt) mustBe Rational(largeInt + 1, largeInt)
    }

    "zero rationals and reals" in {
      val zero1: Rational = 0
      val zero5: Real = 0
      val zero2: Real = r"0/1"
      val zero6: Rational = r"0/1"
      val zero3: Real = Real(0)
      val zero7: Rational = Rational(0)
      val zero4: Rational = Rational(0, 1)

      //      zero1 mustBe zero2
      //      zero1 mustBe zero3
      zero1 mustBe zero4
      //      zero1 mustBe zero5
      zero1 mustBe zero6
      zero1 mustBe zero7

      zero1 - zero2 - zero3 - zero4 - zero5 - zero6 - zero7 mustBe zero2
      zero1 + zero2 + zero3 + zero4 + zero5 + zero6 + zero7 mustBe zero2
    }

    "how to instantiate" in {
      import spire.implicits._
      val r1: Real = 0
      val r2: Real = 1
      val r3: Rational = 1
      val r4: Rational = 0
      val r5: Rational = r"1/2"
      val r6: Real = r"1/2"
      val r7: Real = Real(1)
      val r11: Real = Real("1")
      val r8: Rational = Rational(1)
      val r9: Rational = Rational(1, 1)
      val r10: Rational = Rational("1/2")
      a[NumberFormatException] shouldBe thrownBy(Rational("1/2.asdfasdfa"))
      //won't compile with message NumberFormatException
      //      r"1/asdf"

      //at the moments there is no support for it:
      //      val n0: Natural = 0
      //      val n1: Natural = n"123412341324132412342342341234123412341235235123512351"

      val n2: Natural = Natural("1")
      val n3: Natural = Natural("20098094012304120394809123840918230498123094801293840912384098123049812309481023984")
      n3 - n2 + Natural(1) mustBe n3

      val n4: Natural = Natural(4)
    }

    "how to use numeric" in {
      def f[N: Numeric](n: N) = (n * 2, n - 1, n / 10)
      f(15)     mustBe (30,     14,       1)
      f(15L)    mustBe (30L,    14L,      1L)
      f(15f)    mustBe (30f,    14f,      1.5f)
      f(15d)    mustBe (30d,    14d,      1.5d)
      f(r"1/5") mustBe (r"2/5", r"-4/5",  r"1/50")
    }

    "how to use number" in {

      val n0:    Number = 0
      val n1:    Number = 1
      val n123:  Number = 123
      val n15:   Number = Number("15")
      val big:   Number = Number("15345911174971023470123740917239047123947912374907213947129374912374907123409712390470912374902137")

      val number: Number = n1 / n123
      //      number mustBe n0 // not true anymore
    }

    "how to use SafeLong" in {

      val n0:    SafeLong  = 0
      val n1:    SafeLong  = 1
      val n123:  SafeLong  = 123
      val n5:    SafeLong = SafeLong(5)
      val n6:    SafeLong = SafeLong(6)
      val n3:    SafeLong = SafeLong(3)
      val n15:   SafeLong  = SafeLong (BigInt("15"))
      val n20:   SafeLong  = 20
      val n60:   SafeLong  = 60
      val big:   SafeLong  = SafeLong (BigInt("15345911174971023470123740917239047123947912374907213947129374912374907123409712390470912374902137"))
      val big2:  SafeLong  = BigInt("15345911174971023470123740917239047123947912374907213947129374912374907123409712390470912374902137")

      n1 / n123 mustBe SafeLong.zero

      n1.gcd(n123) mustBe SafeLong.one
      n15.gcd(n6) mustBe n3
      15 gcd 6 mustBe 3

      n15 lcm n3 mustBe n15
      n6 lcm n15 lcm n5 lcm n1 lcm n20 mustBe n60
      n20 lcm n15 lcm n5 lcm n1 lcm n6 mustBe n60      //order doesn't matter
    }
  }

}
