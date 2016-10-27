package howitworks.wip

class STDemo extends wp.Spec {

  //TODO finish it

  import scalaz._
  import effect._


  "Hello ST" in {

    val x: ST[Nothing, STRef[Nothing, Int]] = ST.newVar(1)

  }
}
