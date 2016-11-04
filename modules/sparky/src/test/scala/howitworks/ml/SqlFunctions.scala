package howitworks.ml

import org.apache.spark.sql.expressions.UserDefinedFunction

object SqlFunctions extends Serializable {

  case class Sales(
    id: Int,
    account: String,
    year: String,
    commision: Int,
    salesReps: Seq[String]
  )

}

class SqlFunctions extends wp.SparkySpec {
  import SqlFunctions._

  "explode & udf & pivot" in {
    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    val sales = Seq(
      Sales(1, "Acme", "2013", 1000, Seq("Jim", "Tom")),
      Sales(2, "Lumos", "2013", 1100, Seq("Fred", "Ann")),
      Sales(3, "Acme", "2014", 2800, Seq("Jim")),
      Sales(4, "Lumos", "2014", 1200, Seq("Ann")),
      Sales(4, "Acme", "2014", 4200, Seq("Fred", "Sally"))
    ).toDF()

    sales.select(
      $"id",
      $"account",
      $"year",
      $"commision",
      $"salesReps"
    )
    //      .show()
    //    +---+-------+----+---------+-------------+
    //    | id|account|year|commision|    salesReps|
    //    +---+-------+----+---------+-------------+
    //    |  1|   Acme|2013|     1000|   [Jim, Tom]|
    //    |  2|  Lumos|2013|     1100|  [Fred, Ann]|
    //    |  3|   Acme|2014|     2800|        [Jim]|
    //    |  4|  Lumos|2014|     1200|        [Ann]|
    //    |  4|   Acme|2014|     4200|[Fred, Sally]|
    //    +---+-------+----+---------+-------------+

    //but
    sales.select(
      $"id",
      $"account",
      $"year",
      $"commision", //this will show accumulated shares !!
      explode($"salesReps").as("salesRep") //thaks to that few more rows appears
    )
    //  .show()

    //    +---+-------+----+---------+-----+
    //    | id|account|year|commision|salesRep|
    //    +---+-------+----+---------+-----+
    //    |  1|   Acme|2013|     1000|  Jim|
    //    |  1|   Acme|2013|     1000|  Tom|
    //    |  2|  Lumos|2013|     1100| Fred|
    //    |  2|  Lumos|2013|     1100|  Ann|
    //    |  3|   Acme|2014|     2800|  Jim|
    //    |  4|  Lumos|2014|     1200|  Ann|
    //    |  4|   Acme|2014|     4200| Fred|
    //    |  4|   Acme|2014|     4200|Sally|
    //    +---+-------+----+---------+-----+

    //Example of User Defined Function
    val len = (_: Seq[String]).length

    val column_len: UserDefinedFunction = udf(len)


    sales.select(
      $"id",
      $"account",
      $"year",
      $"commision",
      ($"commision" / column_len($"salesReps")).as("share"), //now commision is splited equally into each salesRep
      explode($"salesReps").as("salesRep")
    )
    //      .show()

    //    +---+-------+----+------+--------+
    //    | id|account|year| share|salesRep|
    //    +---+-------+----+------+--------+
    //    |  1|   Acme|2013| 500.0|     Jim|
    //    |  1|   Acme|2013| 500.0|     Tom|
    //    |  2|  Lumos|2013| 550.0|    Fred|
    //    |  2|  Lumos|2013| 550.0|     Ann|
    //    |  3|   Acme|2014|2800.0|     Jim|
    //    |  4|  Lumos|2014|1200.0|     Ann|
    //    |  4|   Acme|2014|2100.0|    Fred|
    //    |  4|   Acme|2014|2100.0|   Sally|
    //    +---+-------+----+------+--------+

    //now Pivot


    sales.select(
      $"id",
      $"account",
      $"year",
      $"commision",
      ($"commision" / column_len($"salesReps")).as("share"), //now commision is splited equally into each salesRep
      explode($"salesReps").as("salesRep")
    )
      .groupBy($"salesRep")
      .pivot("year") //replace each distinct value of column with new column
      .agg(sum("share")) //here goes the values of new columns
//      .show()

    //voila, sum of shares by sales rep by years

    //    +--------+-----+------+
    //    |salesRep| 2013|  2014|
    //    +--------+-----+------+
    //    |     Tom|500.0|  null|
    //    |     Jim|500.0|2800.0|
    //    |    Fred|550.0|2100.0|
    //    |     Ann|550.0|1200.0|
    //    |   Sally| null|2100.0|
    //    +--------+-----+------+

    //or group by TWO columns:

    sales.select(
      $"id",

    $"account",
    $"year",
    $"commision",
    ($"commision" / column_len($"salesReps")).as("share"), //now commision is splited equally into each salesRep
    explode($"salesReps").as("salesRep")
    )
    .groupBy($"salesRep", $"account")   //<-- aggregation by two columns
      .pivot("year") //replace each distinct value of column with new column
      .agg(sum("share")) //here goes the values of new columns
//      .show()
//    +--------+-------+-----+------+
    //    |salesRep|account| 2013|  2014|
    //    +--------+-------+-----+------+
    //    |     Tom|   Acme|500.0|  null|
    //    |    Fred|   Acme| null|2100.0|
    //    |    Fred|  Lumos|550.0|  null|
    //    |   Sally|   Acme| null|2100.0|
    //    |     Jim|   Acme|500.0|2800.0|
    //    |     Ann|  Lumos|550.0|1200.0|
    //    +--------+-------+-----+------+
  }
}
