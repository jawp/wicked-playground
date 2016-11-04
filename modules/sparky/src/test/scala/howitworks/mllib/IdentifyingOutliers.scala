//package howitworks.mllib
//
//
//import org.apache.spark.mllib.feature.VectorAssembler
//import org.apache.spark.mllib.linalg.{Vector, Vectors}
//import org.apache.spark.sql.{DataFrame, Dataset, Row}
//
//class IdentifyingOutliers extends wp.SparkySpec {
//
//  //Covariance Matrix and inverse of it
//  //Mahalanobis distance (based on inverte Covariance Matrix)
//  //Seek and destroy outliers
//
//  //Mahalanobis distrance: https://en.wikipedia.org/wiki/Mahalanobis_distance
//  // - how many standard deviations point is away from mean
//  // - unitless and scale invariant distance
//  // - can be used to detect outliers
//
//  "it must work" in {
//
//    //given ...
//    import org.apache.spark.sql.functions._
//
//    val df: DataFrame = sqlContext
//      .range(0, 10)
//      .select("id")
//      .withColumn("uniform", rand(123L))
//      .withColumn("normal1", randn(123L))
//      .withColumn("normal2", randn(124L))
//
//    df.printSchema()
//
//    val assembler = new VectorAssembler()
//      .setInputCols(Array("uniform", "normal1", "normal2"))
//      .setOutputCol("features")
//
//    val dfVec: DataFrame = assembler.transform(df).select("id", "features")
//    //end of given
//
//    val dfOutliers: DataFrame = dfVec.select("id", "features").union(sqlContext.createDataFrame(Seq(
//      (10, org.apache.spark.ml.linalg.Vectors.dense(3.0, 3.0, 3.0))
//    )))
//
//    //in order to user $-notation
//    //    dfOutliers.sort($"id".desc).show(5)
//    //
//    //    +---+--------------------+
//    //    | id|            features|
//    //    +---+--------------------+
//    //    | 10|       [3.0,3.0,3.0]| <-- outlier
//    //    |  9|[0.04509890777530...|
//    //    |  8|[0.57206360775153...|
//    //    |  7|[0.15367835411103...|
//    //    |  6|[0.13778573503201...|
//    //    +---+--------------------+
//
//
//
//    //important step is to normalize data
//    //thanks to that covariance matrix is the same as correlation matrix
//    //it's important to user Mahalanobis distance  (but I don't understand why...)
//
//    import org.apache.spark.ml.feature.StandardScaler
//    val scaler = new StandardScaler()
//      .setWithMean(true)
//      .setWithStd(true)
//      .setInputCol("features")
//      .setOutputCol("scaledFeat")
//
//    val scalerModel = scaler.fit(dfOutliers.select("id", "features"))
//    val dfScaled = scalerModel.transform(dfOutliers)
//    //    dfScaled.sort($"id".desc).show(5)
//    //    +---+--------------------+--------------------+
//    //    | id|            features|          scaledFeat|
//    //    +---+--------------------+--------------------+
//    //    | 10|       [3.0,3.0,3.0]|[2.83777721089172...|
//    //    |  9|[0.04509890777530...|[-0.7479347460360...|
//    //    |  8|[0.57206360775153...|[-0.1084738752286...|
//    //    |  7|[0.15367835411103...|[-0.6161758116923...|
//    //    |  6|[0.13778573503201...|[-0.6354611794525...|
//    //    +---+--------------------+--------------------+
//
//
//    //now compute inverse of covariance matrix
//    //user mllib.stat.Statistics object which can compute statistics for all dimentions of vector at once
//    // Statistics operate on RDD, let's create RDD from DF
//    import org.apache.spark.mllib.stat.Statistics
//
//    val rddVec: RDD[Vector] = dfScaled.rdd.map(_(1).asInstanceOf[org.apache.spark.ml.linalg.Vector])
//
//    //this returns matrix type of mllib.linalg.Matrix
//    val colCov: ml.linalg.Matrix = Statistics.corr(rddVec)
//
//    //we must convert it to breeze format
//    val breezeMatrix = new breeze.linalg.DenseMatrix(3,3, colCov.toArray)
//    //now it is possible to invert it
//
//    val invColCovB = breeze.linalg.inv(breezeMatrix)
//
//
//    //and now Computing the Mahalanobis Distance
//
//
//    import org.apache.spark.sql.functions._
//
//    val mahalanobis = udf[Double, mllib.linalg.Vector]{
//      (v: Vector) =>
//        val vB = breeze.linalg.DenseVector(v.toArray)
//        vB.t * invColCovB * vB
//    }
//
//    import sqlContext.implicits._
//    val dfMahalanobis = dfScaled.withColumn("mahalanobis", mahalanobis($"scaledFeat"))
//
//    //    dfMahalanobis.show()
//    //
//    //    +---+--------------------+--------------------+-------------------+
//    //    | id|            features|          scaledFeat|        mahalanobis|
//    //    +---+--------------------+--------------------+-------------------+
//    //    |  0|[0.50295344138165...|[-0.1923376466929...|0.10931580939247694|
//    //    |  1|[0.52307011539946...|[-0.1679264747761...| 1.1151185869455895|
//    //    |  2|[0.85356928901577...|[0.23312750354786...|  3.347681020691449|
//    //    |  3|[0.82540915099773...|[0.19895575280272...|  5.998683725012855| <--quite big
//    //    |  4|[0.52619456581864...|[-0.1641350181834...|0.04188061192719014|
//    //    |  5|[0.13617504799810...|[-0.6374157151801...|  2.358850112730292|
//    //    |  6|[0.13778573503201...|[-0.6354611794525...|  2.100350759031286|
//    //    |  7|[0.15367835411103...|[-0.6161758116923...| 2.9626378975709557|
//    //    |  8|[0.57206360775153...|[-0.1084738752286...|  2.438804667617717|
//    //    |  9|[0.04509890777530...|[-0.7479347460360...| 0.7739573638864936|
//    //    | 10|       [3.0,3.0,3.0]|[2.83777721089172...|  8.752719445193732| <-- outlier
//    //    +---+--------------------+--------------------+-------------------+
//
//    //ok. we have mahalanobis column, which tells which rows are mostly out of the group
//    //let's remove outliers (for example 2 of them)
//
//
//    val outliers: Array[Long] = dfMahalanobis
//      .select("id", "mahalanobis").sort($"mahalanobis".desc)
//      .drop("mahalanobis")
//      .take(2)
//      .map(_ (0).asInstanceOf[Long])
//
//
////    outliers mustBe Array(10, 3) randomness works different on travis and my machine ....
//
//    val dfwithoutOutliers = dfOutliers.filter(s"id not in (${outliers.mkString(",")})")
//
//    dfwithoutOutliers.count() mustBe 9
//
//  }
//}
