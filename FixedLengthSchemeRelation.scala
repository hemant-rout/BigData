/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.spark.fixedlength

import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.InputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SQLContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._

case class FixedLengthSchemeRelation(location: Option[String],
                                     charset: String,
                                     quote: String,
                                     safe: Boolean,
                                     strict: Boolean,
                                     dateFormats:  List[FastDateFormat],
                                     lengthsAndDelimiters: String,
                                     nullValue: String,
                                     treatEmptyValuesAsNullsFlag: Boolean,
                                     userSchema: StructType
                              )(@transient val sqlContext: SQLContext)
  extends BaseRelation with TableScan {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[FixedLengthSchemeRelation])
  override val schema: StructType = userSchema

  override def buildScan: RDD[Row] = {

    val sc = sqlContext.sparkContext
    implicit val conf: Configuration = sc.hadoopConfiguration
    conf.setBoolean("mapred.mapper.new-api", false)
    conf.setClass("mapred.input.format.class", classOf[FixedWidthInputFormat], classOf[InputFormat[_, _]])
    conf.set("charsetName", charset)
    conf.set("quote", quote)
    conf.set("lengthsAndDelimiters", lengthsAndDelimiters)
    val input = sc.hadoopFile(location.get, classOf[FixedWidthInputFormat], classOf[LongWritable], classOf[Text])

    val tokens:RDD[Array[AnyRef]] = {

        input.values.map( line =>
          try{
          FixedWidthHelper.getFields(schema,
            line.toString, lengthsAndDelimiters.split(","),
            safe, quote, dateFormats)
          } catch {
            case e:Exception => {
              throw new RuntimeException(e )
            }
          })

    }

    tokens.flatMap { t => {
      Some(Row.fromSeq(t))
    }

    }
  }

}
