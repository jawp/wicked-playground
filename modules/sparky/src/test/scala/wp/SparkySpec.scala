package wp

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, SharedSparkContext}
import org.apache.spark.SparkConf

trait SparkySpec extends Spec with DataFrameSuiteBase {

  //see http://teknosrc.com/spark-error-java-io-ioexception-could-not-locate-executable-null-bin-winutils-exe-hadoop-binaries/
  System.setProperty("hadoop.home.dir", "d:\\devstation-workspace\\bin\\winutils\\")

  override val conf = new SparkConf().
    setMaster("local"). //it will create only one worker (was local[*])
    setAppName("test").
    set("spark.ui.enabled", "false").
    set("spark.app.id", appID)
}