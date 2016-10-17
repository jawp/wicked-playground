package howitworks.spire

import spire.random._

class DistDemo extends wp.Spec {
  "hello dist" in {

    val r: Random.type = spire.random.Random

    val a: r.R[String] = r.constant("X")
    //the same but unaliased
    val a1: Random[String, rng.Cmwc5] = r.constant("X")

    a.run() mustBe "X"
    a.run() mustBe "X"

    val b: Random[(String, String), rng.Cmwc5] = for {
      b1 <- a
      b2 <- a
    } yield (b1, b2)

    b.run() mustBe ("X", "X")
    b.run(Seed.zero) mustBe ("X", "X")
    b.run(Seed(10)) mustBe ("X", "X")

    //tbc ...
  }

}
