package wip

class ApplicativeFunctorDemo extends wp.Spec {

  //TODO finish it

  "it must work" in {
    //applicative functor has to extra operations, pure and apply (later ap)
    //def pure[A](a:A): F[A] creates Gawk form a value
    //aff - aplicative functor function (wrapped funtion)
    //afv - aplicative functor value (wrapped value)
    //def ap[A,B](aff: F[A=>B], afv: F[A]): F[B]

    //instances:
    //type=Option, pure(a)=Some(a), ap =(aff, afv)=> if(aff.isDevined && afv.isDefined) Some(aff.get.apply(afv.get)) else None
    //why we need ApplicativeFunctor for Option? We can lift all aff, afv into Option and forget about nulls.
    //so it makes error progation for you

    //list is an applicative functor:
    //ap(aff, afv) = aff.foldMap(f => afv.map(f(_))), pure(a)= List(a)
    //other applicative functors:
    //future, continuations, exception-style errors (probably validations), parsers, behaviours in functioanl reactive programming

    //parser:
    //Parser[T].read: T

    //how they gonna make my coding live better
    //how would these going to make coding live better
  }
}
