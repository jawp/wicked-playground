package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import howitworks.mllib.StatisticsOnDataFrame.Record
import org.apache.spark.sql.{DataFrame, SQLContext}

import scalaz.syntax.id._

class RandomData extends wp.Spec with SharedSparkContext {

  "DataFrames" in {
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    import org.apache.spark.sql.functions._

    val randDF = sqlContext.range(0, 10)
      .select("id")
      .withColumn("uniform", rand(seed=123L))
      .withColumn("normal", randn(seed=123L))

//    randDF.show()

  }
}
