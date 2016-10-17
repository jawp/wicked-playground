package exercise

/**
  *
  * Given basic inheritance structure and basis methods write derived functions for each trait.
  *
  * 1. Functor
  * 2. Apply
  * 3. Applicative
  * 4. Monad
  *
  * Use it as kata.
  */
class E2_DerivedFunctions {


  object possibleSolution {
    //TODO: finish this

    trait Functor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }

    trait Apply[F[_]] extends Functor[F] {
      def ap[A, B](fa: F[A])(f: F[A => B]): F[B]
    }

    trait Applicative[F[_]] extends Apply[F] {
      def pure[A](a: A): F[A]
    }

    trait Monad[F[_]] extends Applicative[F] {
      def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B]
    }
  }

}
