package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD

import scalaz.syntax.id._

class Splitting extends wp.Spec with SharedSparkContext {

  "random split RDD into subRDDs" in {

    val dataSize = 10000
    val data = sc.parallelize(1 to dataSize)

    //split data into 3 groups each with size 0.6, 0.2 and 0.2 (for example for training, test and verifying purposes)
    val splits: Array[RDD[Int]] = data.randomSplit(weights = Array(.6, .2, .2), seed = 123L)

    splits.length mustBe 3

    val training = splits(0)
    val test = splits(1)
    val validation = splits(2)

    //splits are approximate
    splits
      .map(_.count().toDouble/dataSize.toDouble) mustBe Array(0.599894, 0.200262, 0.199844) withClue "more or less 0.6 0.2 and 0.2"

  }
}
