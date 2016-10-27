//import cats.data.State
//
//package object rng {
//
//  type RNGState[A] = State[RNG, A]
//
//  object long {
//    val next: RNGState[Long] = State { rng => (rng.next, rng.run) }
//    val nextPositive: RNGState[Long] = next.map(math.abs)
//
//    def nextScoped(min: Long, max: Long): RNGState[Long] = {
//      require(max >= min)
//      nextPositiveLong.map(x => if(min == max) min else (x % (max-min)) + min)
//    }
//
//    def nextIntWithinRange(min: Int, max: Int): State[RNG, Int] = nextLongWithinRange(min, max).map(_.toInt)
//
//  }
//
//
//}
