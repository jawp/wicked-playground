package howitworks.spark

import org.apache.spark.{SparkConf, SparkContext}

object HelloSpark extends App {
  val conf = new SparkConf().setAppName("Simple Application").setMaster("local")
  val sc = new SparkContext(conf)

  wordCount(sc)

  private def wordCount(sc: SparkContext): Unit = {
    sc.parallelize(FileUtil.load("sialababamak.txt"))
      .flatMap(_.split(" "))
      .filter(_ != "")
      .map(w => (w.toLowerCase, 1))
      .reduceByKey((a, b) => a + b)
      .collect()
      .sortBy(_._2)
      .reverse
      .take(20)
      .foreach { case (word, count) =>
        println(s"$word: $count")
      }
  }

  object FileUtil {
    import java.nio.charset.Charset
    import java.nio.file.{Files, Paths}
    import scala.collection.JavaConversions._

    def load(name: String): Seq[String] = {
      val path = Paths.get("modules", "sparky", "data", name)
      Files.readAllLines(path, Charset.defaultCharset()).filter(_ != "")
    }
  }
}
