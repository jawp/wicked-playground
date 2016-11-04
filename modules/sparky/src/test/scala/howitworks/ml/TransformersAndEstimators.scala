package howitworks.ml

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.feature.{Tokenizer, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.DataFrame

class TransformersAndEstimators extends wp.SparkySpec {

  "example - tokenizer" in {

    //Transformer is an algorithm which can transform DataFrame into DataFrame
    //transform(df) - the main method

    val sentenceDataFrame0: DataFrame = spark.sqlContext.createDataFrame(Seq(
      (0, "You rock! - said Rule"),
      (1, "You rule! - said Rock")
    ))

    sentenceDataFrame0.columns mustBe Array("_1", "_2")

    val sentenceDataFrame = sentenceDataFrame0.toDF("label", "sentence")

    sentenceDataFrame.columns mustBe Array("label", "sentence")

    val tokenizer: Transformer = new Tokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")

    //and main call

    val tokenized = tokenizer.transform(sentenceDataFrame)

    //    tokenized.show()
    //    +-----+--------------------+--------------------+
    //    |label|            sentence|               words|
    //    +-----+--------------------+--------------------+
    //    |    0|You rock! - said ...|[you, rock!, -, s...|
    //    |    1|You rule! - said ...|[you, rule!, -, s...|
    //    +-----+--------------------+--------------------+
  }

  "example estimator" in {

    //A Estimator is algorithm which can fit to DataFrame and create Transformer
    //Abstracts the concept of a algorithm which trains itself on data
    //fit() - the main method, which accepts dataframe and produces model (transformer)

    val trainings = spark.sqlContext.createDataFrame(Seq(
      (0.0, Vectors.dense(0.0, 1.1, 0.1)),
      (0.0, Vectors.dense(2.0, 1.0, -1.0)),
      (0.0, Vectors.dense(2.0, 1.3, 1.0)),
      (1.0, Vectors.dense(0.0, 1.2, -0.5))
    )).toDF("label", "features")

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.01)


    val model1: LogisticRegressionModel = lr.fit(trainings)

    //alternatively params can be passed as map:
    val params = ParamMap(
      lr.maxIter -> 10,
      lr.regParam -> 0.01
    )

    val model2 = lr.fit(trainings, params)
    //    +-----+--------------+--------------------+--------------------+----------+
    //    |label|      features|       rawPrediction|         probability|prediction|
    //    +-----+--------------+--------------------+--------------------+----------+
    //    |  0.0| [0.0,1.1,0.1]|[1.16982694929573...|[0.76311373457421...|       0.0|
    //    |  0.0|[2.0,1.0,-1.0]|[2.61850937288521...|[0.93204335305876...|       0.0|
    //    |  0.0| [2.0,1.3,1.0]|[9.29998138736924...|[0.99990858242479...|       0.0|
    //    |  1.0|[0.0,1.2,-0.5]|[-0.9791253740505...|[0.27306536303270...|       1.0|
    //    +-----+--------------+--------------------+--------------------+----------+

    //every model is transform
    //this is how you run predictions
    //    model1.transform(trainings).show
  }

  "vector assembler" in {
    //A VectorAssebmler fuses multiple columns into one vector column
    import org.apache.spark.sql.functions._

    val df = spark.sqlContext
      .range(0, 10)
      .select("id")
      .withColumn("uniform", rand(123L))
      .withColumn("normal1", randn(123L))
      .withColumn("normal2", randn(124L))

    val assembler = new VectorAssembler()
      .setInputCols(Array("id", "uniform", "normal1", "normal2"))
      .setOutputCol("features")
    val dfVec = assembler.transform(df)

    //dfVec.show()
    //    +---+-------------------+--------------------+-------------------+--------------------+
    //    | id|            uniform|             normal1|            normal2|            features|
    //    +---+-------------------+--------------------+-------------------+--------------------+
    //    |  0| 0.5029534413816527|0.001988081602007817|0.08548815017388822|[0.0,0.5029534413...|
    //    |  1| 0.5230701153994686| 0.08548815017388822| 1.0812373243612423|[1.0,0.5230701153...|
    //    |  2| 0.8535692890157796|  1.0812373243612423| 1.3016155984173403|[2.0,0.8535692890...|
    //    |  3|   0.82540915099773|  1.3016155984173403|-1.0079961211050452|[3.0,0.8254091509...|
    //    |  4| 0.5261945658186415| 0.10477657238210662|0.29758209867176094|[4.0,0.5261945658...|
    //    |  5|0.13617504799810343| -1.0079961211050452| -0.381225633796665|[5.0,0.1361750479...|
    //    |  6|0.13778573503201175|  -0.381225633796665| -1.158142357575387|[6.0,0.1377857350...|
    //    |  7|0.15367835411103337|  -1.158142357575387|0.08968324318745649|[7.0,0.1536783541...|
    //    |  8|  0.572063607751534| 0.08968324318745649| 1.5666908171945368|[8.0,0.5720636077...|
    //    |  9|0.04509890777530612| -0.5661249353611272|0.11865373272107547|[9.0,0.0450989077...|
    //    +---+-------------------+--------------------+-------------------+--------------------+

    //or
    //dfVec.select("id", "features").show()
  }
}

