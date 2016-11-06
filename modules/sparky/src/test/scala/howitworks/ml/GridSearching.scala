package howitworks.ml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier


class GridSearching extends wp.SparkySpec {

  //for finding best params of the Estimator (model)
  //for each combination of parameter it will evaluate a model so we can peak the winner

  "it must work" ignore {   //todo

    //given some model
    val rf = new RandomForestClassifier()
    //rf.extractParamMap() //this will show all params:

    import org.apache.spark.ml.tuning.ParamGridBuilder

    val paramGrid = new ParamGridBuilder()
      .addGrid(rf.maxDepth, Array(10, 30))
      .addGrid(rf.numTrees, Array(10, 100))
      .build()

    //validating models:

    //given pipeline:
    val pipeline = new Pipeline()
      .setStages(Array()) // todo

    import org.apache.spark.ml.tuning.CrossValidator
    val cv = new CrossValidator()
      .setEstimator(pipeline)
      //.setEvaluator(auc_eval) //TODO
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(3)  //see k-fold cross validation https://en.wikipedia.org/wiki/Cross-validation_(statistics)

  }
}
