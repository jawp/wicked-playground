package howitworks.ml

class GradientBoostingTrees extends wp.SparkySpec {

  //GBT
  //

  "it must work" in {

    import org.apache.spark.ml.Pipeline
    import org.apache.spark.ml.evaluation.RegressionEvaluator
    import org.apache.spark.ml.feature.VectorIndexer
    import org.apache.spark.ml.regression.{GBTRegressionModel, GBTRegressor}


    val gbt = new GBTRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)


    //Params:
    //log loss for classification, squared and absolute
    // for regression previously gbt.loss
    gbt.lossType

    gbt.maxIter //num of trees in ensemble, previously numiterations
    //gbt.learningRate - should not be tuned, decrease only to improove stability


    //gbt can overfit when to much trees
    //use runWithValidation(trainingRDD, validationRDD) to avoid it
    //validationTol -

    //read the scaladoc of:
    lazy val x: org.apache.spark.mllib.tree.configuration.BoostingStrategy = ???

    //IOs - only one output, no probability

    //pipeline - the same as for DT

    //GBTrees vs Random Forestes
    //num of trees:
    // - RFs - more trees reduces variance, improves preformance monotnically
    // - GBTs - more trees reduces bias, but increases the likelyhood of overfitting (decrease performance)
    //parallelization:
    // - RFs: can train multiple trees in parallel
    // - GBTs: one tree at once
    //depth of trees:
    // - RFs: deeper trees
    // - BGTs: shallower trees

  }
}

