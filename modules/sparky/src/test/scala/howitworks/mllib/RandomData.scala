package howitworks.mllib


import org.apache.spark.sql.DataFrame

class RandomData extends wp.SparkySpec {

  "DataFrames" in {

    import org.apache.spark.sql.functions._

    val randDF: DataFrame = sqlContext.range(0, 10)
      .select("id")
      .withColumn("uniform", rand(seed=123L))
      .withColumn("normal", randn(seed=123L))

//    randDF.show()

  }
}
