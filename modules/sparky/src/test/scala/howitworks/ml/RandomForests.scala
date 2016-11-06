package howitworks.ml


class RandomForests extends wp.SparkySpec {

  //Random Forests
  //default IOs, pipeline and parameters

  //Ensemble - is a model containing set of other models
  //RF and GBT are top performers for classification and regression problems
  //they are ensembles
  //RF:
  //support multi class and binary classification/regression
  //support categorical and continouse features
  //algorithm: train several DTs based on some subset
  //  of training data (which is choosen independently in each
  // iteration (the bootstrapping), this reduced the variance
  // prediction/classification is than based on majority voting or
  // on simple average (in regression)
  //
  //
  "it must work" in {
    import org.apache.spark.ml.Pipeline
    import org.apache.spark.ml.evaluation.RegressionEvaluator
    import org.apache.spark.ml.feature.VectorIndexer
    import org.apache.spark.ml.regression.{RandomForestRegressionModel, RandomForestRegressor}

    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    //RF parameters:
    rf.numTrees //training time increases almost linearly
    rf.maxDepth //more expressive but takes longer to train and is more prone to overfitting


    //the fraction of the original set used for training (default 1.0)
    //decreasing speeds up the learning time
    rf.subsamplingRate

    //fraction of number of features
    // to use as candidates for splitting at each tree node
    // if set to low can impact performance
    rf.featureSubsetStrategy

    //IOs - the same as in DecisionTrees


  }
}
