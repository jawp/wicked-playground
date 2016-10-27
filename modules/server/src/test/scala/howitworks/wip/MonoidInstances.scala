package howitworks.wip

class MonoidInstances extends wp.Spec {

  "it must work" in {

    //below forms monoids:
    //type=Number, op=multiply, zero=0
    //type=Number, op=plus, zero=0
    //type=Natural, op=max, zero=0 (max(max(a,b), c) == max(a,(max(b,c)), netural element is 0 max(0, x) == max(x, 0) == x
    //type=Int, op=math.max, zero=Int.MinimumValue
    //type=List[A], op=concatenate, zero=Nil
    //type=Option[A], op=(o1, o2) => if(o1.isDefined) o1 else o2, zero=None
    //type=Option[A:Monoid], op=(o1, o2) => if (o1.isEmpty) o2 else if(o2.isEmpty) o1 else Some(Monoid[A].op(o1.get, o2.get))
    //type=Function1[A,A] (endofuntion) op=compose, zero=identity
    //type=Function[A,B:Monoid], op=(f1, f2) => (a => Monoid[B].op(f1(a),f2(a)) ), zero= _ => Monoid[B].zero
    //???n-heap is a monoid
    //type=Functor[_] op = (F1, F2) => F1 compose F2, zero=Functor[Id]
    //??? function composition? f1: A => B and f2: B => C f1 + f2 =f3

  }
  "what you can do with monoids" in {
    //you can squash many instances together and get one instance (fold)
    //split computations, and combine resutls (map-reducem, it should be named functor-monoid)
  }
  "propertieses" in {
    //they scale very vell
    //they compose via functinos, optionals and other things
  }
}
