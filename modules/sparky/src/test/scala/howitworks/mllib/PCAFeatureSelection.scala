package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}

import scalaz.syntax.id._

class PCAFeatureSelection extends wp.Spec with SharedSparkContext {

  //Principal Component Analysis
  //https://pl.wikipedia.org/wiki/Analiza_g%C5%82%C3%B3wnych_sk%C5%82adowych
  //For feature selection - many features, choose the most important set of them
  //choosen set of features (called principal components) ideally should not depend on each other

  //see feature engineering https://en.wikipedia.org/wiki/Feature_engineering

  //Why PCA
  // Pros:
  // + Interpretability
  // + PCA creates components that are uncorrelated, and some predictive models prefer to no colinearity (example Linear Regression)
  // + helps to avoiding the 'curse of dimentionality'
  //    classifiers tend to overfit the training data in high
  //    dimentional spaces, so reducint the numbers of dimentions may help
  // + less data to process (performance)
  // + for classification problems can show potential separation (if there is such)
  // Cons:
  // - effort of computation depends on number of records and features
  // - it will consider first data with more variance (like income), than other independend (like foot size) with less variance
  //    (thus it's recommended to normalize data before PCA)
  //
  // Advice: Always scale and center predictors before PCA
  // how many PCs use?


  "it must work" in {

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val crimes: DataFrame = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("modules/sparky/data/UScrime2-colsLotsOfNAremoved.csv")
      .drop("community")   //string
      .drop("OtherPerCap") //string


    //since we don't want DataFrame but matrix, let's convert it
    import org.apache.spark.mllib.linalg.{Vector => SVector}


    val assembler = new VectorAssembler()
      .setInputCols(crimes.columns)
      .setOutputCol("features")

    val featuresDF = assembler.transform(crimes).select("features")

    //featuresDF.show(10)


    val rddOfRows: RDD[Row] = featuresDF.rdd
    val rddOfVectors: RDD[SVector] = rddOfRows.map(row => row.get(0).asInstanceOf[SVector])

    val mat = new RowMatrix(rddOfVectors)


    //compute top 10 Principal Components

    //voila, pc is a local matrix
    val pc: Matrix = mat.computePrincipalComponents(10)

    (pc.numRows, pc.numCols) mustBe (crimes.columns.length, 10)

    //TODO: what next (how to make predictions when model is trained based on computed PCs?)

  }
}
