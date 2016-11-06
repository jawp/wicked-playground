package howitworks.ml


class ModelEvaluation extends wp.SparkySpec {

  //Evaluators:
  // - when your model is fit and ready to predict - evaluator is something which
  //    will evaluate model (predict, classify...)
  // - ROC (Receiver operating characteristic) curve https://en.wikipedia.org/wiki/Receiver_operating_characteristic
  // - summaries and metrics - are used to evaluate models
  // - Evaluator is class that computes metrics from the predicions:

  //Metrics:
  //  - BinaryClassificationEvaluator
  //    - expects columns: rawPrediction, label
  //    - supports metric: areaUnderROC
  //  - MulitClassClasssificatinEvaluator
  //    - F1 (default), Precision, Recall, weightPrecision, weightRecall
  //    - expects columns: prediction, label
  //  - RegressionEvaluator
  //    - expects columns: predicion, label
  //    - params:
  //      - rmse(default root mean square error),
  //      - mse (mean square err),
  //      - r2 (the coeficient of determination),
  //      - mae (mean absolute square)

  //Summaries (4 LogisticRegression :
  // LogisticRegressionTrainingSummary, metrics are:
  //  - areaUnderROC
  //  - fMeasureByThreshold
  //  - pr
  //  - precisionByThreshold
  //  - recallByThreshold
  //  - roc
  // BinaryLogisticRegressinSummary (can be casted from above)

  // Summaries (4 LinearRegression):
  // - explainedVariance
  // - meanAbsoluteError
  // - meanSquareError
  // - r2
  // - residuals
  // - rootMeanSquaredError


  "it must work" in {

  }
}
