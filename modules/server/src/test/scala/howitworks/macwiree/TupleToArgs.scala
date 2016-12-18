package howitworks.macwiree

class TupleToArgs extends wp.Spec {

  def f(i: Int, s: String, d: Double): String = s"$i $s $d"

  "how to apply a function to a tuple" in {
    val tuple = (10, "abc", 11.02)
    (f _).tupled(tuple)
  }
}
