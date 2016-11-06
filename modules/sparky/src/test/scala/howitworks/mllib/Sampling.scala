package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.mllib.linalg.distributed.IndexedRow
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD

class Sampling extends wp.SparkySpec {

  //Goal: given RDD you want to have random subset of this RDD
  //you can customise
  // * the probability of selecting row from origin into subset
  // * whether elements are selected with replacements or not
  // * stratified sampling -> map of probabilities per group (https://en.wikipedia.org/wiki/Stratified_sampling)

  "sampling" in { //seed doesn't work when running from sbt

    val data: RDD[Vector] = sc.parallelize (Seq(
      Vectors.dense(1,2,3),
      Vectors.dense(4,5,6),
      Vectors.dense(7,8,9),
      Vectors.dense(10,11,12)
    ))

    data.sample(withReplacement = false, fraction = 0.5, seed = 11L)
      .collect()
    //not deteministic
    //    mustBe Array(
    //      Vectors.dense(1.0,2.0,3.0)
    //    ) withClue "it should sampled only one row"

    data.sample(withReplacement = false, fraction = 0.5, seed = 123L)
      .collect()
    //not deteministic mustBe Array() withClue "it should sampled no rows in this case"

    data.sample(withReplacement = false, fraction = 0.5, seed = 12L)
      .collect() //not deteministic  mustBe Array(
//      Vectors.dense(1.0,2.0,3.0),
//        Vectors.dense(4.0,5.0,6.0),
////      Vectors.dense(7.0,8.0,9.0),
//        Vectors.dense(10.0,11.0,12.0)
//    ) withClue "it should sampled all but one rows in this case"

    data.sample(withReplacement = false, fraction = 0.5, seed = 10L)
      .collect() //not deterministic
//    mustBe Array(
//      Vectors.dense(1.0,2.0,3.0),
//      Vectors.dense(4.0,5.0,6.0),
//      Vectors.dense(7.0,8.0,9.0),
//      Vectors.dense(10.0,11.0,12.0)
//    ) withClue "it should sampled all rows in this case"

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

    //stratum = group of pairs with the same key

    // Get an approximate sample from each stratum
    val sampleGroups0: RDD[(Int, Char)] =
    data.sampleByKey(withReplacement = false, fractions = fractions, seed = 123L)
    //sampleGroups0.collect() mustBe Array((2, 'd'), (2, 'e')) doesn't work well on distributed env

    //sampleByKey propoption is guaranteed with 99.9% confidence
    val sampleGroups1: RDD[(Int, Char)] =
      data.sampleByKeyExact(withReplacement = false, fractions = fractions, seed = 123L)
    sampleGroups1.collect() //not deterministic mustBe Array((1,'a'), (2,'d'), (2,'e'), (3,'f'))
  }

  "stratified sampling RDD[IndexedRow]" in {

    val rows = sc.parallelize(Array(
      IndexedRow(0L, Vectors.dense(1,2,3)),       //  0
      IndexedRow(1L, Vectors.dense(11,22,33)),    //  1
      IndexedRow(1L, Vectors.dense(111,222,333))) //  1
    )

    rows
      .map { case IndexedRow(key, vec) => (key, vec) } //first map into tuple
      .sampleByKey(withReplacement = false, fractions = Map(0L -> 1.0, 1L -> .5), seed = 11L)
      .collect()
    //seed doesn't work when running from sbt
    //mustBe Array(
    //        0 -> Vectors.dense(1.0,2.0,3.0),
    //        1 -> Vectors.dense(11.0,22.0,33.0)
    //      )
  }

}
