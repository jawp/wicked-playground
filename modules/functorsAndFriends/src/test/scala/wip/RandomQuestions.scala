package wip

import rng.RNG


class RandomQuestions extends wp.Spec {


  "random questions " in {
    import cats.data.State

    type StateRNG[A] = State[RNG, A]

    val nextLong: State[RNG, Long] = State { rng => (rng.next, rng.run) }
    val nextPositiveLong: State[RNG, Long] = nextLong.map(math.abs)

    sealed trait Op
    case object Plus extends Op
    case object Minus extends Op
    case object Multi extends Op
    val allOps = Array(Plus, Minus, Multi)

    val nextOperator: State[RNG, Op] = nextPositiveLong.map( x => allOps((x % 3).toInt))

    type Challenge = String
    type ExpectedAns = Long
    type ChallengeResult = Long

    def opToQuestion(a: Long, b: Long, op: Op): (Challenge, ExpectedAns) = op match {
      case Plus => (s"What is $a + $b?", a+b)
      case Minus => (s"What is $a - $b?", a-b)
      case Multi => (s"What is $a * $b?", a*b)
    }

    val nextQuestion: StateRNG[(Challenge, ExpectedAns)] = for {
      a <- nextLong
      b <- nextLong
      op <- nextOperator
    } yield opToQuestion(a, b, op)


    sealed trait QuizState

    case class Initial(name: String, rng: RNG) extends QuizState

    case class GotToken(token: String, name: String) extends QuizState
    case class Question(token: String, name: String, no: Int) extends QuizState
    case class Failed(token: String, name: String, no: Int)
    case class Success(token: String)

    def nextToken(name: String) = nextPositiveLong.map(n => s"name$n")

    def quiz(answerHook: String => String): State[RNG, Challenge] = for {
      t <- nextQuestion
    } yield {
      val answer = answerHook(t._1)
      if(answer == t._2) "Great Success" else "Spot"
    }


  }
}
