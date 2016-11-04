package howitworks.mllib


import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vector, Vectors}
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.mllib.stat.test.ChiSqTestResult
import org.apache.spark.rdd.RDD

class HypothesisTesting extends wp.SparkySpec {

  //introduction in polish
  //http://edu.pjwstk.edu.pl/wyklady/adn/scb/wyklad7/w7.xml

  "Pearson's chi-squared test - hypothesis testing" in {

    // a vector composed of the frequencies of events
    val vec: Vector = Vectors.dense(0.1, 0.15, 0.2, 0.3, 0.25)

    // Compute the "goodness of fit" (https://en.wikipedia.org/wiki/Goodness_of_fit).
    // If a second vector to test against is not supplied
    // as a parameter, the test runs against a uniform distribution (as in this case).
    //
    // Pearson's chi-squared test is used to assess two types
    // of comparison: tests of goodness of fit and tests of independence.

    val goodnessOfFitTestResult: ChiSqTestResult = Statistics.chiSqTest(vec)

    goodnessOfFitTestResult
      .toString() mustBe "Chi squared test summary:\n" +
                         "method: pearson\n" +
                         "degrees of freedom = 4 \n" +
                         "statistic = 0.12499999999999999 \n" +
                         "pValue = 0.998126379239318 \n" +
                         "No presumption against null hypothesis: observed follows the same distribution as expected.."


    //example 2, trying construct not uniformed distributed frequencies of events.
    val vec2: Vector = Vectors.dense(0.9, 0.02, 0.02, 0.02, 0.02, 0.02)

    //This is what Pearson test is telling:
    Statistics.chiSqTest(vec2)
      .toString() mustBe "Chi squared test summary:\n" +
                         "method: pearson\n" +
                         "degrees of freedom = 5 \n" +
                         "statistic = 3.8720000000000008 \n" +
                         "pValue = 0.5679882371140674 \n" +
                         "No presumption against null hypothesis: observed follows the same distribution as expected.."

  }


  "independent testing - contingency table" in {
    //https://en.wikipedia.org/wiki/Contingency_table

    //here is a contingency table of two random variables. One with two outcomes and one with 3 outcomes
    //We will test whether these variables are independent or not
    //This can help features selection

    val mat: Matrix = Matrices.dense(3, 2,
      Array(
        1.0, 3.0, 5.0, //first col
        2.0, 4.0, 6.0  //second col
      ))

    // conduct Pearson's independence test on the input contingency matrix
    val independenceTestResult: ChiSqTestResult = Statistics.chiSqTest(mat)
    independenceTestResult
      .toString() mustBe "Chi squared test summary:\n" +
                         "method: pearson\n" +
                         "degrees of freedom = 2 \n" +
                         "statistic = 0.14141414141414144 \n" +
                         "pValue = 0.931734784568187 \n" +
                         "No presumption against null hypothesis: the occurrence of the outcomes is statistically independent.."

    //variables aren't independent


    //here is a conitngency table of other two random variables (as above)
    val mat2: Matrix = Matrices.dense(3, 2, Array(
      13, 47, 40, //first col
      80, 11, 9   //second col
    ))

    Statistics.chiSqTest(mat2)
      .toString() mustBe "Chi squared test summary:\n" +
                         "method: pearson\n" +
                         "degrees of freedom = 2 \n" +
                         "statistic = 90.22588968846716 \n" +
                         "pValue = 0.0 \n" +
                         "Very strong presumption against null hypothesis: the occurrence of the outcomes is statistically independent.."

    //here is a conitngency table of other two random variables (as above)
    val mat3: Matrix = Matrices.dense(3, 2, Array(
      13, 2, 88, //first col
      13, 88, 45   //second col
    ))

    Statistics.chiSqTest(mat3)
      .toString() mustBe "Chi squared test summary:\n" +
                         "method: pearson\n" +
                         "degrees of freedom = 2 \n" +
                         "statistic = 91.37945790463658 \n" +
                         "pValue = 0.0 \n" +
                         "Very strong presumption against null hypothesis: the occurrence of the outcomes is statistically independent.."

  }

  //  "independence testing - RDD of labeled points" in
  //  problems in spark 2.0 after migration from 1.6
  //    val obs: RDD[LabeledPoint] = sc.parallelize(Seq(
  //      LabeledPoint(0L, spark.mllib.linalg.Vectors.dense(1, 2)),
  //      LabeledPoint(0L, spark.mllib.linalg.Vectors.dense(0.5, 1.5)),
  //      LabeledPoint(1L, spark.mllib.linalg.Vectors.dense(1.0, 8.0))
  //    ))
  //
  //    val featureTestResults: Array[ChiSqTestResult] = Statistics.chiSqTest(obs)
  //    featureTestResults.length mustBe 2
  //  }

  "Test whethere two probability distributions are equal - Kolmogorov-Smirnov test" ignore {
    //seed doesn't work when running from sbt
    //supported distributions: norm and CDF (customized cumulative density function)
    import org.apache.spark.mllib.random.RandomRDDs

    val data: RDD[Double] = RandomRDDs.normalRDD(sc = sc, size = 100, numPartitions = 1, seed = 13L)
    val testResult = Statistics.kolmogorovSmirnovTest(data, "norm", 0.0, 1.0)
    testResult
      .toString() mustBe "Kolmogorov-Smirnov test summary:\n" +
                         "degrees of freedom = 0 \n" +
                         "statistic = 0.12019890461912125 \n" +
                         "pValue = 0.10230385223938121 \n" +
                         "No presumption against null hypothesis: Sample follows theoretical distribution."

    //but when using uniformedDistribution test not passes
    val data2: RDD[Double] = RandomRDDs.uniformRDD(sc = sc, size = 100, numPartitions = 1, seed = 13L)
    val testResult2 = Statistics.kolmogorovSmirnovTest(data2, "norm", 0.0, 1.0)
    testResult2
      .toString() mustBe "Kolmogorov-Smirnov test summary:\n" +
                         "degrees of freedom = 0 \n" +
                         "statistic = 0.5022419691869352 \n" +
                         "pValue = -2.220446049250313E-16 \n" + //very small pValue
                         "Very strong presumption against null hypothesis: Sample follows theoretical distribution."

  }


}
