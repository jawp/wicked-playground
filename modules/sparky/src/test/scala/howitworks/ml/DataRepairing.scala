package howitworks.ml

class DataRepairing extends wp.SparkySpec {

  "create spoiled data and repair it" in {

    import org.apache.spark.sql.functions._

    val halfToNan = udf[Double, Double](x => if (x > 0.5) Double.NaN else x)
    val oneToNan = udf[Double, Double](x => if (x > 1.0) Double.NaN else x)

    val df = sqlContext
      .range(0, 10)
      .select("id")
      .withColumn("uniform", rand(123L))
      .withColumn("normal", randn(123L))

    //    df.show()

    val spoiledDF = df
      .withColumn("uniform2", halfToNan(df("uniform")))
      .withColumn("normal2", oneToNan(df("normal")))
      .drop("uniform")
      .drop("normal")
      .withColumnRenamed("uniform2", "uniform")
      .withColumnRenamed("normal2", "normal")


    //    spoiledDF.show()
    //    +---+-------------------+--------------------+
    //    | id|            uniform|              normal|
    //    +---+-------------------+--------------------+
    //    |  0|                NaN|0.001988081602007817|
    //      |  1|                NaN| 0.08548815017388822|
    //      |  2|                NaN|                 NaN|
    //      |  3|                NaN|                 NaN|
    //      |  4|                NaN| 0.10477657238210662|
    //      |  5|0.13617504799810343| -1.0079961211050452|
    //      |  6|0.13778573503201175|  -0.381225633796665|
    //      |  7|0.15367835411103337|  -1.158142357575387|
    //      |  8|                NaN| 0.08968324318745649|
    //      |  9|0.04509890777530612| -0.5661249353611272|
    //      +---+-------------------+--------------------+

    //Dropping rows:
    spoiledDF.na.drop()
    spoiledDF.na.drop("all", Seq("uniform", "normal")) //all uniform and normal must be null in order to drop row
    spoiledDF.na.drop("any", Seq("uniform", "normal")) //any uniform either normal must be null in order to drop
    spoiledDF.na.drop(minNonNulls = 3)

    //Repairing rows:

    //repair with 0.0
    spoiledDF.na.fill(0.0)

    //Repair with mean
    val meanUniform = spoiledDF.filter("uniform <> 'NaN'").groupBy().agg(mean("uniform")).first()(0)

    spoiledDF
      .na
      .fill(Map("uniform" -> meanUniform))
    //.show()

    //count both mean and repair with them
    val spoiledDFCols = spoiledDF.columns.drop(1)

    val means = spoiledDF
      .na
      .drop() //filters all rows where at least on value is NaN
      .groupBy()
      .agg(mean("uniform"), mean("normal"))
      .first().toSeq

    spoiledDF
      .na
      .fill(spoiledDFCols.zip(means).toMap)
    //.show()

    //replace values

    spoiledDF
      .na
      .replace("uniform", Map(Double.NaN -> 0.0))

    spoiledDF
      .union(spoiledDF)
      //.show()
      .dropDuplicates() //returns the same as spoiledDF
  }
}
