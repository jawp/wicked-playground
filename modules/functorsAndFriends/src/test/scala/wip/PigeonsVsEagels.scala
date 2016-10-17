package wip

import cats.data.OptionT
import rng.{Cmwc5, RNG}


class PigeonsVsEagels extends wp.Spec {



  "pigeons vs eagels" in {
    import cats.data.State
    import cats.instances.list._
    import cats.instances.option._
    import cats.syntax.traverse._

    type StateRNG[A] = State[RNG, A]

    val nextLong: State[RNG, Long] = State { rng => (rng.next, rng.run) }
    val nextPositiveLong: State[RNG, Long] = nextLong.map(math.abs)

    def nextLongWithinRange(min: Long, max: Long): State[RNG, Long] = {
      require(max >= min)
      nextPositiveLong.map(x => if(min == max) min else (x % (max-min)) + min)
    }

    def nextIntWithinRange(min: Int, max: Int): State[RNG, Int] = nextLongWithinRange(min, max).map(_.toInt)



    List.fill(1000)(nextLongWithinRange(-400L, +500L)).sequenceU.runA(Cmwc5.default).value
      .filter(x => x < -400 || x > 500) mustBe List() withClue " there must be no items out of scope"

    nextLongWithinRange(0L, 0L).runA(Cmwc5.default).value mustBe 0L
    nextLongWithinRange(10L, 10L).runA(Cmwc5.default).value mustBe 10L

    val nextDouble: State[RNG, Double] = nextLong.map(java.lang.Double.longBitsToDouble)

    //next double in [0.0, 1.0).
    val nextDouble01: State[RNG, Double] = nextLong.map(x => (x >>> 11) * 1.1102230246251565e-16)
    //next double -1.0 or +1.0
    val nextDoubleSign: State[RNG, Double] = nextLong.map(x => if (x < 0) -1.0 else 1.0)

    //next double (-1.0, 1.0)
    val `nextDouble(-1,1)`: State[RNG, Double] = for {
      d    <- nextDouble01
      sign <- nextDoubleSign
    } yield d * sign


    //sanity checks
    List.fill(20)(`nextDouble(-1,1)`).sequenceU.runA(Cmwc5.default)
      .value mustBe List(-0.29316522241869947, -0.9697575525067785, 0.3068910261461185, -0.7702207907655839, 0.5553678830019705, -0.4432261118112225, -0.28424319784829355, -0.08791050640342224, -0.5670936912087755, -0.9768893061441374, -0.7029143005768838, -0.05164059676953758, -0.4367337078823904, 0.8549730811337944, -0.3433512512200432, -0.8475146263698287, 0.9332563422003102, -0.5769324196846992, -0.7697366225549156,
      -0.7114157338601429) withClue " example values differ"

    //sanity checks
    List.fill(1000)(`nextDouble(-1,1)`).sequenceU.runA(Cmwc5.default).value
      .filter(x => x < -1.0 || x > 1.0 ) mustBe List() withClue " there must be no items out of scope"

    //[min,max] (inclusive)
    def nextDoubleWithinRange(min: Double, max: Double): State[RNG, Double] = nextDouble01.map { x =>
      require(min <= max)
      val distance = max - min
      (x * distance) + min
    }

    //sanity checks
    nextDoubleWithinRange(1.2401, 1.2401).runA(Cmwc5.default).value mustBe 1.2401

    //sanity checks
    List.fill(10)(nextDouble).sequenceU.runA(Cmwc5.default).value mustBe List(3.4572161265504856E53, -1.880662019667126E-96, -1.9051316263851603E271, -1.641203669455796E-52, 2.831491392235305E70, 4.56631116003441E-160, -1.7644117361033466E25, -1.4365442467425055E286, -2.1437851331099934E-240, 5.857929779579694E211)

    //sanity checks
    List.fill(1000)(nextDoubleWithinRange(-1000.0, 21.5)).sequenceU.runA(Cmwc5.default).value
      .filter(x => x <= -1000.0 || x >= 21.5 ) mustBe List() withClue " there must be no items out of scope"

    //results in x +- variance, where  min < x < max
    def nextWithinRangeWithVariance(min: Double, max: Double, `+- variance`: Double): State[RNG, Double] = for {
      base     <- nextDoubleWithinRange(min, max)
      variance <- nextDoubleWithinRange(-`+- variance`, `+- variance`)
      random = base + variance
    } yield random

    //sanity checks
    List.fill(1000)(nextWithinRangeWithVariance(-1000.0, 21.5, 50.0)).sequenceU.runA(Cmwc5.default).value
      .filter(x => x <= (-1000.0-50.0) || x >= (21.5+50.0) ) mustBe List() withClue " there must be no items out of scope"

    def `nextDouble(-n,n)`(n: Double): State[RNG, Double] = `nextDouble(-1,1)`.map(x => n * x)

    //model
    type BirdKey = Int
    type Bird = Attack
    type LifePoint = Int

    case class Attack(value: Double)

    object Attack {
      val variance: Double = 50.0
      def nextAttack(min: Attack, max: Attack): State[RNG, Attack] = nextDoubleWithinRange(min.value, max.value).map(Attack.apply)
    }

    case class Bravery(value: Double)

    object Bravery {
      val variance: Double = 20.0
      def nextBravery(min: Bravery, max: Bravery): State[RNG, Bravery] = nextDoubleWithinRange(min.value, max.value).map(Bravery.apply)
    }

    case class BirdGroup(
                          aliveKeys: List[BirdKey],
                          attack: Map[BirdKey, Attack],
                          bravery: Map[BirdKey, Bravery],
                          lifePoints: Map[BirdKey, LifePoint]
                        ) {
      def size: Int = attack.size
      def isAlive(key: BirdKey): Boolean = lifePoints.get(key).get > 0
    }

    def nextBirdGroup(
                       size: Int,
                       minAttack: Attack = Attack(1.0),
                       maxAttack: Attack = Attack(100.0),
                       minBravery: Bravery = Bravery(2.0),
                       maxBravery: Bravery = Bravery(20.0)
                     ): State[RNG, BirdGroup] = for {
      attack <- List.fill(size)(Attack.nextAttack(minAttack, maxAttack)).sequenceU
      bravery <- List.fill(size)(Bravery.nextBravery(minBravery, maxBravery)).sequenceU
    } yield BirdGroup(
      aliveKeys = (0 until size).toList,
      attack = attack.zipWithIndex.map(_.swap).toMap,
      bravery = bravery.zipWithIndex.map(_.swap).toMap,
      lifePoints = List.fill(size)(3).zipWithIndex.map(_.swap).toMap
    )

    //sanity checks
    val exampleGroupSize = 10
    val exampleGroup: BirdGroup = nextBirdGroup(exampleGroupSize).runA(Cmwc5.default).value
    exampleGroup.size mustBe exampleGroupSize
    exampleGroup.isAlive(0) mustBe true
    exampleGroup.aliveKeys.size mustBe exampleGroupSize
    exampleGroup.aliveKeys.toSet mustBe exampleGroup.attack.keySet
    exampleGroup.aliveKeys.toSet mustBe exampleGroup.bravery.keySet
    exampleGroup.aliveKeys.toSet mustBe exampleGroup.lifePoints.keySet
    exampleGroup.attack.size mustBe exampleGroupSize
    exampleGroup.bravery.size mustBe exampleGroupSize
    exampleGroup.lifePoints.size mustBe exampleGroupSize

    //TODO algebra for attack, bravery, min, max etc

    def nextIndex(list: List[_]): State[RNG, Option[Int]] =
      (if (list.isEmpty) None else Some(list.size))
        .map (size => nextIntWithinRange(0, size))
        .sequenceU

    //sanity checks
    nextIndex(Nil).runA(Cmwc5.default).value mustBe None
    nextIndex(List('a)).runA(Cmwc5.default).value mustBe Some(0)
    List.fill(1000)(nextIndex(List.fill(100)('whatever))).sequenceU.runA(Cmwc5.default)
      .value.map(_.get).filter(x => x < 0 || x > 99) mustBe List() withClue " no values out of scope, all defined"

    def next2Indexes(l: List[_]): State[RNG, Option[(Int, Int)]] = for {
      maybeIndex1 <- nextIndex(l)
      maybeIndex2 <- maybeIndex1.map(index1 => l.patch(index1, Nil, 1)).map(x => nextIndex(x)).sequenceU.map(_.flatten)
    } yield {
      maybeIndex1.flatMap(i1 => maybeIndex2.map{i2 =>
        val normalizedI2 = if(i2 < i1) i2 else i2+1
        (i1, normalizedI2)
      })
    }


    //sanity checks
    //    next2Indexes(Nil).runA(Cmwc5.default).value mustBe None
    //    next2Indexes(List('a)).runA(Cmwc5.default).value mustBe None
    next2Indexes(List('a, 'b)).runA(Cmwc5.default).value mustBe Some((1,0))
    next2Indexes(List('a, 'b, 'c)).runA(Cmwc5.default).value mustBe Some((1,0))
    next2Indexes(List('a, 'b, 'c, 'c)).runA(Cmwc5.default).value mustBe Some((1,0))
    List.fill(1000)(next2Indexes(List.fill(100)('whatever))).sequenceU.runA(Cmwc5.default)
      .value.map(_.get).filter{case (i1, i2) =>
      i1 == i2 ||
        i1 < 0 ||
        i2 < 0 ||
        i1 > 99 ||
        i2 > 99
    } mustBe List() withClue " no values out of scope, all defined"
    List.fill(20)(next2Indexes(List.fill(100)('whatever))).sequenceU.runA(Cmwc5.default)
      .value.map(_.get) mustBe List((17,46), (40,70), (76,78), (53,76), (8,75), (53,80), (12,47), (50,46), (98,14), (59,75), (3,78), (44,53), (53,27), (59,57), (21,65), (72,67), (35,17), (95,69), (95,48), (71,58))


    def next2Indexes2(l: List[_]): OptionT[StateRNG, (Int, Int)] = for {
      i1 <- OptionT[StateRNG, Int](nextIndex(l))
      i2 <- OptionT[StateRNG, Int](nextIndex(l.patch(i1, Nil, 1)))
    } yield {
      val normalizedI2 = if(i2 < i1) i2 else i2+1
      (i1, normalizedI2)
    }

    //sanity checks
    next2Indexes2(List('a, 'b)).value.runA(Cmwc5.default).value mustBe Some((1,0))
    next2Indexes2(List('a, 'b, 'c)).value.runA(Cmwc5.default).value mustBe Some((1,0))
    next2Indexes2(List('a, 'b, 'c, 'c)).value.runA(Cmwc5.default).value mustBe Some((1,0))
    List.fill(1000)(next2Indexes2(List.fill(100)('whatever)).value).sequenceU.runA(Cmwc5.default)
      .value.map(_.get).filter{case (i1, i2) =>
      i1 == i2 ||
        i1 < 0 ||
        i2 < 0 ||
        i1 > 99 ||
        i2 > 99
    } mustBe List() withClue " no values out of scope, all defined"
    List.fill(20)(next2Indexes2(List.fill(100)('whatever)).value).sequenceU.runA(Cmwc5.default)
      .value.map(_.get) mustBe List((17,46), (40,70), (76,78), (53,76), (8,75), (53,80), (12,47), (50,46), (98,14), (59,75), (3,78), (44,53), (53,27), (59,57), (21,65), (72,67), (35,17), (95,69), (95,48), (71,58))



    def selectFighters(birdGroup: BirdGroup): State[RNG, Option[(BirdKey, BirdKey)]] = next2Indexes(birdGroup.aliveKeys)
      .map(_.map{ case (i1, i2) => (birdGroup.aliveKeys(i1), birdGroup.aliveKeys(i1))})
    //
    //
    //    def iterate(birdGroup: BirdGroup): State[RNG, BirdGroup] = {
    //
    //    }
    //
    //    /**
    //      * @return winner
    //      */
    //    def fight(a: BirdKey, b: BirdKey, birdGroup: BirdGroup): State[RNG, BirdKey] = for {
    //      augmnentAttackA <- `nextDouble(-n,n)`(Attack.variance)
    //      augmnentAttackB <- `nextDouble(-n,n)`(Attack.variance)
    //      attackA = birds(a).value + augmnentAttackA
    //      attackB = birds(b).value + augmnentAttackB
    //    } yield (if (attackA >= attackB) a else b)



  }}
