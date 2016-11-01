package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD

class Sampling extends wp.Spec with SharedSparkContext {

  //Goal: given RDD you want to have random subset of this RDD
  //you can customise
  // * the probability of selecting row from origin into subset
  // * whether elements are selected with replacements or not
  // * stratified sampling -> map of probabilities per group (https://en.wikipedia.org/wiki/Stratified_sampling)

  "sampling" in {

    val data: RDD[Vector] = sc.parallelize (Seq(
      Vectors.dense(1,2,3),
      Vectors.dense(4,5,6),
      Vectors.dense(7,8,9),
      Vectors.dense(10,11,12)
    ))

    data.sample(withReplacement = false, fraction = 0.5, seed = 11L)
      .collect() mustBe Array(
      Vectors.dense(1.0,2.0,3.0)
    ) withClue "it sampled only one row"

    data.sample(withReplacement = false, fraction = 0.5, seed = 123L)
      .collect() mustBe Array() withClue "no rows were sampled in this case"

    data.sample(withReplacement = false, fraction = 0.5, seed = 12L)
      .collect() mustBe Array(
      Vectors.dense(1.0,2.0,3.0),
        Vectors.dense(4.0,5.0,6.0),
//        Vectors.dense(7.0,8.0,9.0),
        Vectors.dense(10.0,11.0,12.0)
    ) withClue "it sampled all but one rows in this case"

    data.sample(withReplacement = false, fraction = 0.5, seed = 10L)
      .collect() mustBe Array(
      Vectors.dense(1.0,2.0,3.0),
      Vectors.dense(4.0,5.0,6.0),
      Vectors.dense(7.0,8.0,9.0),
      Vectors.dense(10.0,11.0,12.0)
    ) withClue "it sampled all rows in this case"

  }

  "Stratified Sampling" in {


    val data = sc.parallelize(Seq(
      (1, 'a'), (1, 'b'),
      (2, 'c'), (2, 'd'), (2, 'e'),
      (3, 'f')
    ))

    // specify the exact fraction desired from each key
    val fractions = Map(
      1 -> 0.1,
      2 -> 0.6,
      3 -> 0.3
    )
    // Get an approximate sample from each stratum
    val approxSample0: RDD[(Int, Char)] =
    data.sampleByKey(withReplacement = false, fractions = fractions, seed = 123L)

    approxSample0.collect() mustBe Array((2, 'd'), (2, 'e'))
  }

}
