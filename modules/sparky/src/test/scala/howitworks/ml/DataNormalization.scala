package howitworks.ml

import org.apache.spark.ml.feature.{StandardScalerModel, VectorAssembler}

class DataNormalization extends wp.SparkySpec {

  //It's good practice to normalize dataset before training model
  // - normalize to have p-unit form (Normalizer)
  // - normalize to have unit standard deviation and/or zero mean (StandardScaler)
  // - normalize to have given minimum/maximum (MinMaxScaler)

  //A Normalizer is a transformer

  "Normalizer, Standard Scaler" in {

    //A Normalizer users P-norm to normalize data

    //given ...
    import org.apache.spark.sql.functions._

    val df = sqlContext
      .range(0, 10)
      .select("id")
      .withColumn("uniform", rand(123L))
      .withColumn("normal1", randn(123L))
      .withColumn("normal2", randn(124L))

    val assembler = new VectorAssembler()
      .setInputCols(Array("id", "uniform", "normal1", "normal2"))
      .setOutputCol("features")
    val dfVec = assembler.transform(df).select("id", "features")

    //see VectorSlicer - which can extract specific dimensions from vector
    //end of given

    import org.apache.spark.ml.feature.Normalizer

    val scaler1 = new Normalizer()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setP(1.0) // p := 1, vectors will have unit norml L1, see https://en.wikipedia.org/wiki/Norm_(mathematics)

    val dfScaled1 = scaler1
      .transform(dfVec)
//      .show()
    //note scaled are bigger, cuz original features were randomly selected from 0.0-1.0 range
    //    +---+--------------------+--------------------+
    //    | id|            features|      scaledFeatures|
    //    +---+--------------------+--------------------+
    //    |  0|[0.0,0.5029534413...|[0.0,0.8518430970...|
    //    |  1|[1.0,0.5230701153...|[0.37177546269391...|
    //    |  2|[2.0,0.8535692890...|[0.38194017195467...|

    //ok, how to denormalize? just use the orignal features vecors?


    //now StandardScaler

    import org.apache.spark.ml.feature.StandardScaler
    //not ...mllib.feature.StandardScaler


    val scaler2 = new StandardScaler() //this is an Estimator
      .setWithMean(true)
      .setWithStd(true)
      .setInputCol("features")
      .setOutputCol("scaledFeatures")

    val scalerModel2: StandardScalerModel = scaler2.fit(dfVec) //this is a Transformer

    val dfScaled2 = scalerModel2.transform(dfVec)

    //dfScaled2.show()
    //
    //    +---+--------------------+--------------------+
    //    | id|            features|      scaledFeatures|
    //    +---+--------------------+--------------------+
    //    |  0|[0.0,0.5029534413...|[-1.4863010829205...|
    //    |  1|[1.0,0.5230701153...|[-1.1560119533826...|
    //    |  2|[2.0,0.8535692890...|[-0.8257228238447...|


    //and these are model params, you can use them to revert back to origin
    scalerModel2.mean
    scalerModel2.std

    //and finally the MinMaxScaler

    //WARN: zero values will be transformed to non-zero values,
    //so all SPARE vectors become DENSE

    import org.apache.spark.ml.feature.MinMaxScaler
    val scaler3 = new MinMaxScaler() //it's an Estimator
      .setMin(-1.0)
      .setMax(1.0)
      .setInputCol("features")
      .setOutputCol("scaledFeatures")

    val scalerModel3 = scaler3.fit(dfVec) //it's a Transformer
    val dfScaled3 = scalerModel3.transform(dfVec)
    //    dfScaled3.show()
    //    +---+--------------------+--------------------+
    //    | id|            features|      scaledFeatures|
    //    +---+--------------------+--------------------+
    //    |  0|[0.0,0.5029534413...|[-1.0,0.132643926...|
    //    |  1|[1.0,0.5230701153...|[-0.7777777777777...|
    //    |  2|[2.0,0.8535692890...|[-0.5555555555555...|
    //    |  3|[3.0,0.8254091509...|[-0.3333333333333...|

    //if interested these are model params so you can use them to revert back to origin
    scalerModel3.originalMax
    scalerModel3.originalMin
  }
}
