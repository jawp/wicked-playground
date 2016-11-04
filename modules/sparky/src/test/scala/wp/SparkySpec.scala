package wp

import com.holdenkarau.spark.testing.DataFrameSuiteBase

trait SparkySpec extends Spec with DataFrameSuiteBase {

  //see http://teknosrc.com/spark-error-java-io-ioexception-could-not-locate-executable-null-bin-winutils-exe-hadoop-binaries/
  System.setProperty("hadoop.home.dir", "d:\\devstation-workspace\\bin\\winutils\\")
}