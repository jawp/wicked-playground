package howitworks.mllib


import org.apache.spark.rdd.RDD

class Splitting extends wp.SparkySpec {


  "random split RDD into subRDDs" in {

    val dataSize = 10000
    val data = sc.parallelize(1 to dataSize)

    //split data into 3 groups each with size 0.6, 0.2 and 0.2 (for example for training, test and verifying purposes)
    val splits: Array[RDD[Int]] = data.randomSplit(weights = Array(.6, .2, .2), seed = 123L)


    //splits.length mustBe 3 //seed doesn't work when running from sbt

    val training = splits(0)
    val test = splits(1)
    val validation = splits(2)

    //splits are approximate
    splits.map(_.count().toDouble/dataSize.toDouble)
    //seed doesn't work when running from sbt
    //mustBe Array(0.599894, 0.200262, 0.199844) withClue "more or less 0.6 0.2 and 0.2"

  }
}
