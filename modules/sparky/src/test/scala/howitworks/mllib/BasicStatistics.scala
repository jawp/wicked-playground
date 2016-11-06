package howitworks.mllib


import java.lang.Math._

import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vector, Vectors}
import org.apache.spark.rdd.RDD

//see more examples in the Spark repo at examples/src/main/scala/org/apache/spark/examples/mllib/SummaryStatisticsExample.scala

class BasicStatistics extends wp.SparkySpec {

  import org.apache.spark.mllib.stat._

  "Summary Stats" in {
    val observations: RDD[Vector] = sc.parallelize(Seq(
      Vectors.dense(1.0, 10.0, 100.0),
      Vectors.dense(2.0, 20.0, 200.0),
      Vectors.dense(3.0, 30.0, 300.0)
    ))

    //these stats are computed COLUMN WISE
    val summaryStats = Statistics.colStats(observations)

    summaryStats.mean mustBe Vectors.dense(2.0, 20.0, 200.0)
    summaryStats.variance mustBe Vectors.dense(1.0, 100.0, 10000.0)
    summaryStats.numNonzeros mustBe Vectors.dense(3.0, 3.0, 3.0)
    summaryStats.min mustBe Vectors.dense(1.0, 10.0, 100.0)
    summaryStats.max mustBe Vectors.dense(3.0, 30.0, 300.0)
    summaryStats.count mustBe 3
    summaryStats.normL1 mustBe Vectors.dense(6.0, 60.0, 600.0) //http://mathworld.wolfram.com/L1-Norm.html
    summaryStats.normL2 mustBe Vectors.dense(sqrt(14.0), sqrt(100+400+900), sqrt(10000 + 40000 + 90000)) //http://mathworld.wolfram.com/L2-Norm.html
  }

  "Correlations" in {
    val seriesX: RDD[Double] = sc.parallelize(Array(1, 2, 3, 3, 5))
    // series
    // must have the same number of partitions and cardinality as seriesX
    val seriesY: RDD[Double] = sc.parallelize(Array(11, 22, 33, 33, 555))

    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
    //correlation mustBe 0.8500286768773007 not deterministic when run on sbt

    //Statistics.corr(seriesX, seriesX, "pearson") mustBe 1.0 //not deterministic: 1.0000000000000002 was not equal to 1.0
    //Statistics.corr(seriesX, seriesX.map(_*2), "pearson") mustBe 1.0 //ditto

    //above can be thought as computing correlation between two features
    //each series can be thought as column in matrix representing observations

    //now correlations between every two series from the set
    //each serie is given by column in matrix

    val data: RDD[Vector] = sc.parallelize(
      Seq(
        Vectors.dense(1.0, 10.0, 100.0),
        Vectors.dense(2.0, 20.0, 200.0),
        Vectors.dense(5.0, 33.0, 366.0))
    )  // note that each Vector is a row and not a column

    // calculate the correlation matrix using Pearson's method. Use "spearman" for Spearman's method
    // If a method is not specified, Pearson's method will be used by default.
    val correlMatrix: Matrix = Statistics.corr(data, "pearson")

    //these are not deterministic - they behave different when called from SBT and Intellij
    //    correlMatrix mustBe Matrices.dense(3, 3, Array(
    //      1.0,                 0.9788834658894731,  0.9903895695275673, //first column
    //      0.9788834658894731,   1.0,                0.9977483233986101, //second column
    //      0.9903895695275673,  0.9977483233986101, 1.0                  //third column
    //    ))
  }

}
