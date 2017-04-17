package howitworks.cats.exercise

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


      //alias for map
      def product[A, B](fa: F[A])(f: A => B): F[(A, B)] = map(fa)(a => (a, f(a)))
      def apply[A, B](fa: F[A])(f: A => B): F[B] = map(fa)(f)
      def xmap[A,B](fa: F[A], f: A => B, g: B => A): F[B] = map(fa)(f)
      //lift f into F
      def lift[A, B](f: A => B): F[A] => F[B] = map(_)(f)
//      def lift[A, B](f: A => B): F[A] => F[B] = x => map(x)(f)
      def mapply[A, B](a: A)(f: F[A => B]): F[B] = map(f)((ff:(A => B)) => ff(a))

    }

    trait Apply[F[_]] extends Functor[F] {
      def ap[A, B](fa: F[A])(f: F[A => B]): F[B]

      //note that f is in F
      def ap2[A, B, C](fa: F[A], fb: F[B])(f: F[(A, B) => C]): F[C] = ap(fb)(ap(fa)(map(f)(_.curried)))
      //f is pure function
      def apply2[A,B,C](fa: F[A], fb: F[B])(f: (A,B) => C): F[C] = ap(fb)(map(fa)(f.curried))

      //syntax for apply from scalaz
      //def ^[A,B,C](fa: => F[A], fb: => F[B])(f: (A, B) => C): F[C] = F.apply2(fa, fb)(f)

      //alias for apply2 (cats version of apply2)
      def map2[A,B,C](fa: F[A], fb: F[B])(f: (A,B) => C): F[C] = apply2(fa, fb)(f)

      def tuple2[A,B](fa: F[A], fb: F[B]): F[(A, B)] = apply2(fa, fb)((_,_))
      def product[A,B](fa: F[A], fb: F[B]): F[(A,B)] = tuple2(fa, fb) //alias (cats version of tuple2)

      def litf2[A, B, C](f: (A, B) => C): (F[A], F[B]) => F[C] = (fa, fb) => apply2(fa, fb)(f)

      def apF[A, B](fa: F[A => B]): F[A] => F[B] = ap(_)(fa)

    }

    trait Applicative[F[_]] extends Apply[F] {
      def pure[A](a: A): F[A]
    }

    trait Monad[F[_]] extends Applicative[F] {
      def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B]
    }
  }

}
