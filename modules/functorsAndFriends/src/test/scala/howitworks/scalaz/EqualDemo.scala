package howitworks.scalaz

//import scalaz.syntax.id._

class EqualDemo extends wp.Spec {

  //about scalaz.Equal

  //inside wp.Spec `===` is taken from  org.scalactic.Equalizer.=== instead of scalaz.syntax.EqualOps
  //here inside spec ≟ and ≠ aliases can be used

  "simple types" in {

    //import guide:

    import scalaz.syntax.all._

    //or
    //val equalOps = new scalaz.syntax.ToEqualOps {}
    //import equalOps._

    import scalaz.std.AllInstances._

    //import scalaz.std.anyVal._  or more detailed

    222 ≟ 222
    //"222" ≟ 222 won't compile

    //unsugared version of abouve
    ToEqualOps(222)(scalaz.std.anyVal.intInstance).===(222)

  }

  "customTypeDemo" in {

    class Foo(val bar: String)
    def Foo(bar: String) = new Foo(bar)


    import scalaz._
    import scalaz.syntax.all._

    //first create Equal instance in scope
    implicit val fooEqual = Equal.equal[Foo]((a, b) => a.bar == b.bar)

    //now you can use it's goodies

    //`===` is taken from  org.scalactic.Equalizer.=== instead of scalaz.syntax.EqualOps
    //Foo("bar") === Foo("bar")
    Foo("bar") ≟ Foo("bar")

    Foo("bar") =/= Foo("bazzzz")
    Foo("bar") ≠ Foo("bazzzz")

    //Foo("bar") ≠ "fobar" won't compile
  }

  "assert_===" in {
    class Foo(val bar: String)
    def Foo(bar: String) = new Foo(bar)
    import scalaz._
    import scalaz.syntax.all._
    implicit val fooEqual = Equal.equal[Foo]((a, b) => a.bar == b.bar)

    //in order to use assert_=== show inscance must be in scope
    implicit val FooShow = Show.shows[Foo](foo => s"Foo(${foo.bar})")

    Foo("a") assert_=== Foo("a")
  }

}
