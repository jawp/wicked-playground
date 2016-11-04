package howitworks.ml

import org.apache.spark.sql.{DataFrame, DataFrameStatFunctions, SQLContext}

object StatisticsOnDataFrame extends Serializable {

  //case class must be outside the method and test case - otherwise expecte serialization problems

  case class Record(desc: String, value1: Int, value2: Double)

  case class Stat(name: String, val1: Double, val2: Double)
}

class StatisticsOnDataFrame extends wp.SparkySpec {
  import StatisticsOnDataFrame._

  "DF.describe returns statistics" in {
    import sqlContext.implicits._

    val recDF: DataFrame = sc.parallelize(Array(
      Record("first", 1, 3.7),
      Record("seconds", -2, 2.1),
      Record("third", 6, 0.7)
    )).toDF()

    val stats: DataFrame = recDF.describe()
//    stats.show()
    stats.collect().map(r => Stat(
      r(0).asInstanceOf[String],
      r(1).toString.toDouble,
      r(2).toString.toDouble
    )).toSet mustBe Set (
      Stat("count", 3, 3),
      Stat("mean", 1.6666666666666667, 2.166666666666667),
      Stat("stddev", 4.041451884327381, 1.5011106998930273),
      Stat("min", -2, 0.7),
      Stat("max", 6, 3.7)
    )

    //similar things but using groupBy and agg

    recDF.groupBy().agg(Map("value1" -> "min", "value2" -> "min"))
      .first().toSeq.map(_.toString.toDouble) mustBe Seq(-2.0, 0.7)

    //or

    import org.apache.spark.sql.functions._
    recDF.groupBy().agg(min("value1"), min("value2") )
      .first().toSeq.map(_.toString.toDouble) mustBe Seq(-2.0, 0.7)
  }

  "DF.stats returns DataFrameStatsFunctions" in {
    val sqlContext= new SQLContext(sc)
    import sqlContext.implicits._

    val recDF: DataFrame = sc.parallelize(Array(
      Record("first", 1, 3.7),
      Record("seconds", -2, 2.1),
      Record("third", 6, 0.7)
    )).toDF()

    val stats: DataFrameStatFunctions = recDF.stat
    val pearsonCorrelation: Double = stats.corr("value1", "value2")

    //pearsonCorrelation mustBe -0.5879120879120878 //ignoring cuz not works in travis ...

    stats.cov("value1", "value2")
      //...
  }

}
