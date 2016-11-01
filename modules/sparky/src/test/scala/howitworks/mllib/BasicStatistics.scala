package howitworks.mllib


import java.lang.Math._

import com.holdenkarau.spark.testing.{SharedSparkContext, SparkContextProvider}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD

import scalaz.syntax.id._

class BasicStatistics extends wp.Spec with SharedSparkContext {

  "Summary Stats" in {
    val observations: RDD[Vector] = sc.parallelize(Seq(
      Vectors.dense(1.0, 10.0, 100.0),
      Vectors.dense(2.0, 20.0, 200.0),
      Vectors.dense(3.0, 30.0, 300.0)
    ))

    //these stats are computed COLUMN WISE
    import org.apache.spark.mllib.stat._
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
}
