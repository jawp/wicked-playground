package howitworks.scalaz

class ReaderDemo extends wp.Spec {

  "hello reader" in {
    import scalaz._
    import Scalaz._

    //compared to cats reader here we don't need to wrap functions into Reader class

    val size: String => Int = _.size
    val ovalLettersCount: String => Int = _.count(List('q', 'Q', 'o', 'O', 'p', 'P', 'd', 'D', 'R', 'a','A','g','b').contains(_))
    val lettersDistribution: String => Map[Char, Int] = _.foldLeft(Map[Char, Int]())((acc, curr) => acc.updated(curr, 1 + acc.getOrElse(curr, 0)))

    //and you would like to create 3rd function based on 'returned values' of these above functions
    //standard approach is:
    val stringAnalysis: String => String = {s =>
      val sSize = size(s)
      val sOvalLettersSize = ovalLettersCount(s)
      val sLetterDistribution = lettersDistribution(s).toList.sortBy(x => (x._1.toUpper, x._1)).mkString(", ")
      s"The string '${s}' contains $sSize letters, $sOvalLettersSize oval letters and in general here this is distribution of letters: $sLetterDistribution"
    }

    //see howitworks.cats.ReaderDemo
    val stringAnalysis3 = for {
      input <- identity[String] _
      size <- size
      ovalCount <- ovalLettersCount
      dist <- lettersDistribution
      distFormatted = dist.toList.sortBy(x => (x._1.toUpper, x._1)).mkString(", ")
    } yield s"The string '$input' contains $size letters, $ovalCount oval letters and in general here this is distribution of letters: $distFormatted"


    val exampleIn = "what are reader monads all about"
    val expectedOut = "The string 'what are reader monads all about' contains 32 letters, 11 oval letters and in general here this is distribution of letters: ( ,5), (a,6), (b,1), (d,2), (e,3), (h,1), (l,2), (m,1), (n,1), (o,2), (r,3), (s,1), (t,2), (u,1), (w,1)"

    stringAnalysis3(exampleIn) mustBe expectedOut
  }
}
