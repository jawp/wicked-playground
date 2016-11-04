package howitworks.ml

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, StringIndexerModel, VectorIndexer}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.DataFrame

class DecisionTrees extends wp.SparkySpec {

  //A word about decision trees:
  // - for classification and regression
  // - easy to interpret
  // - handle categorical features
  // - extend to multi class classification
  // - do NOT require feature scaling
  // - capture non-linearities and feature interactions

  //spark.ml has more functinality than soark.mllib

  "it must work" in {

    //inputs are (label: Double, features: Vector)
    //outputs are (prediction:Double, rawPrediction: Vector, probability: Vector), last to for classification only

    import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}

    //well, this data was somehow prepared in such form ...
    val data: DataFrame = spark.read.format("libsvm").load("modules/sparky/data/sample_libsvm_data.txt")


    //    data.show(10)
    //    +-----+--------------------+
    //    |label|            features|
    //    +-----+--------------------+
    //    |  0.0|(692,[127,128,129...|
    //    |  1.0|(692,[158,159,160...|
    //    |  1.0|(692,[124,125,126...|
    //    |  1.0|(692,[152,153,154...|
    //    |  1.0|(692,[151,152,153...|
    //    |  0.0|(692,[129,130,131...|
    //    |  1.0|(692,[158,159,160...|
    //    |  1.0|(692,[99,100,101,...|
    //    |  0.0|(692,[154,155,156...|
    //    |  0.0|(692,[127,128,129...|
    //    +-----+--------------------+
    //data.printSchema()
    //    root
    //    |-- label: double (nullable = false)
    //    |-- features: vector (nullable = true) //SparseVector

    //pipeline stages:

    val labelIndexer: StringIndexerModel = new StringIndexer() //estimator
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)

    val labelConverter: IndexToString = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)

    val dtC = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")

    val pipelineClass: Pipeline = new Pipeline()
      .setStages(Array(
        labelIndexer,
        featureIndexer,
        dtC,
        labelConverter
      ))

    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    val modelClassifier: PipelineModel = pipelineClass.fit(trainingData)

    //obtain dtC model
    val treeModel = modelClassifier
      .stages(2)  //dtC is at index 2
      .asInstanceOf[DecisionTreeClassificationModel]

    println("Learned classification tree model:\n" + treeModel.toDebugString)

    //this is how we can run predictions
    val predictions = modelClassifier.transform(testData)
    //predictions.show(100)

    //ok, let's see how good is this model:
    // Select (prediction, true label) and compute test error.

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy") //see MulticlassClassificationEvaluator.accuracy

    val accuracy: Double = evaluator.evaluate(predictions)

    println("Test Error = " + (1.0 - accuracy))

  }
}
