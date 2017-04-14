package howitworks.cats


//import cats.kernel.Eq
//import cats.laws.discipline.CartesianTests.Isomorphisms
//import org.scalacheck.rng.Seed
//import org.scalacheck.{Arbitrary, Gen}

class CustomMonadDemo extends wp.Discipline {

  //Sometime one need to create custom monad.
  //How then make sure that this implementation of the monad is correct?
  //We can use discipline library to test that this instance of Monad obeys Monad Laws.
  //my question: http://stackoverflow.com/questions/39561525/how-to-test-monad-instance-using-discipline

  //Fun Monad definition.
  object FunMonad {
    import cats.Monad

    type FUN[A] = Map[String, String] => (List[String], A)
    val funMonad: Monad[FUN] = new Monad[FUN] {
      override def flatMap[A, B](fa: FUN[A])(f: (A) => FUN[B]): FUN[B] = m => {
        val (list1, a1) = fa(m)
        val (list2, a2) = f(a1)(m)
        (list1 ++ list2, a2)
      }
      override def pure[A](x: A): FUN[A] = m => (Nil, x)

      //old implementation
      //override def tailRecM[A, B](a: A)(f: (A) => FUN[Either[A, B]]): FUN[B] = defaultTailRecM(a)(f)
      //@scala.annotation.tailrec
      override def tailRecM[A, B](a: A)(f: (A) => FUN[Either[A, B]]): FUN[B] = ???
    }
  }

  //Here we define RuleSet
  object FunMonadRuleSet {
    import cats.instances.int._
    import cats.instances.list._
    import cats.instances.string._
    import cats.instances.tuple._
    import cats.laws.discipline.CartesianTests.Isomorphisms
    import cats.laws.discipline.MonadTests
    import cats.laws.discipline.eq._

    //or import cats.instances.all._

    import FunMonad._

    //this should be picked up by compiler automatically, but due to some bug its not working
    implicit val funIsomorphisms: Isomorphisms[FUN] = Isomorphisms.invariant(funMonad)

    //this will not work, we must pass implicit param explicitly
    val ruleSet: MonadTests[FUN]#RuleSet = MonadTests[FUN](funMonad).monad[Int, Int, Int]
  }

  //here we run rule set. This will register tests in scalatest
  //TODO: uncomment after implementinc tailRecM
//  checkAll("int", FunMonadRuleSet.ruleSet)
//  the same as above, but using only scalacheck
//  FunMonadRuleSet.ruleSet.all.check
}
