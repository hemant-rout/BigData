package com.spark.fixedlength

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, _}


object FixedLenthPOC {

  def main(args: Array[String]): Unit = {
    val sparkSession:SparkSession = SparkSession.builder.
      master("local")
      .appName("example")
      .config("spark.sql.warehouse.dir", "file:///c:/tmp/spark-warehouse")
      .getOrCreate()

    val schema=StructType(List(StructField("f1", StringType, nullable = true), StructField("f2", StringType, nullable = true), StructField("f3", StringType, nullable = true), StructField("f4", StringType, nullable = true), StructField("f5", StringType, nullable = true)))
    val df = sparkSession.read
      .option("charset", "UTF-8")
      .option("safe", true)
      .option("strict", false)
      .option("lengthsAndDelimiters",args(0))
      .schema(schema)
      .format("com.spark.fixedlength")
      .load(args(1))
df.show()
  }

}
