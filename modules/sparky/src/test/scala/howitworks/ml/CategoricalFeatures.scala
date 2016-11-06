package howitworks.ml

import org.apache.spark.ml.feature.{IndexToString, OneHotEncoder, StringIndexer, StringIndexerModel}
import org.apache.spark.sql.Row

class CategoricalFeatures extends wp.SparkySpec {

  //Categorical data are  like gender: Male, Female
  // String to Number  -see> org.apache.spark.ml.feature.StringIndexer

  "using StringIndexer & IndexToString & OneHotEncoder" in { //ignore because of Travis' java.io.NotSerializableException: org.scalatest.Assertions$AssertionsHelper

    //to convert between String and Numbers

    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    val df = sqlContext.createDataFrame(Seq(
      (0, "US"),
      (1, "UK"),
      (2, "FR"),
      (3, "US"),
      (4, "US"),
      (5, "FR")
    )).toDF("id", "nationality")

    //    df.show()


    // String idexer will count by nationality and create array in descending order of nationalities
    //index of nationality in this array is a decoded integer. This array is caled 'labels'
    //and it is wrapped into StringIndexerModel
    //getting labels out of model it is possible to convert back integer (index) to String
    val indexer = new StringIndexer()
      .setInputCol("nationality")
      .setOutputCol("nIndex")

    val model: StringIndexerModel = indexer.fit(df)
    val indexed = model.transform(df)

    //println(model.labels.toList)
    //    indexed.show()
    //and back

    val converter = new IndexToString()
      .setInputCol("nIndex")
      .setOutputCol("convertedNationality")

    //but wait ... how it knew that 0.0 corresponds to "UK" ??
    //ans: labels: If not provided or if empty, then metadata from inputCol is used instead.")
    converter
      .transform(indexed)
    //      .show()


    //instead of translating categorical data into integere, it translates it into 0,1 vector

    val encoder = new OneHotEncoder()
      .setInputCol("nIndex")
      .setOutputCol("encoding") // <-- this will create sparse vectors

    val encoded = encoder.transform(indexed)
    //      encoded.show()

    //    +---+-----------+------+-------------+
    //    | id|nationality|nIndex|     encoding| as sparse vectors
    //    +---+-----------+------+-------------+
    //    |  0|         US|   0.0|(2,[0],[1.0])|
    //    |  1|         UK|   2.0|    (2,[],[])|
    //    |  2|         FR|   1.0|(2,[1],[1.0])|
    //    |  3|         US|   0.0|(2,[0],[1.0])|
    //    |  4|         US|   0.0|(2,[0],[1.0])|
    //    |  5|         FR|   1.0|(2,[1],[1.0])|
    //    +---+-----------+------+-------------+
    import org.apache.spark.ml.linalg._
    encoded.foreach { (r: Row) =>
      val dv = r.getAs[SparseVector]("encoding").toDense
      //println(s"${r(0)} ${r.getAs[String]("nationality")} $dv")
    }

    //hmm, not really what we needed ...
    //    2 FR [0.0,1.0]
    //    5 FR [0.0,1.0]
    //    0 US [1.0,0.0]
    //    1 UK [0.0,0.0]
    //    3 US [1.0,0.0]
    //    4 US [1.0,0.0]

    //this is why they (vector entries) would be linear depended
    //the missing category is determined by the data in two first columns

    //in order to inlcude last clumn do '.setDropLast(false)':

    val encoder2 = new OneHotEncoder()
      .setInputCol("nIndex")
      .setOutputCol("encoding") // <-- this will create sparse vectors
      .setDropLast(false)

    def toDense = udf[DenseVector, SparseVector](_.toDense)

    encoder2.transform(indexed).withColumn("encoding2", toDense($"encoding"))
//      .show()
//
    //    +---+-----------+------+-------------+-------------+
    //    | id|nationality|nIndex|     encoding|    encoding2|
    //    +---+-----------+------+-------------+-------------+
    //    |  0|         US|   0.0|(3,[0],[1.0])|[1.0,0.0,0.0]|
    //    |  1|         UK|   2.0|(3,[2],[1.0])|[0.0,0.0,1.0]|
    //    |  2|         FR|   1.0|(3,[1],[1.0])|[0.0,1.0,0.0]|
    //    |  3|         US|   0.0|(3,[0],[1.0])|[1.0,0.0,0.0]|
    //    |  4|         US|   0.0|(3,[0],[1.0])|[1.0,0.0,0.0]|
    //    |  5|         FR|   1.0|(3,[1],[1.0])|[0.0,1.0,0.0]|
    //    +---+-----------+------+-------------+-------------+

  }

}