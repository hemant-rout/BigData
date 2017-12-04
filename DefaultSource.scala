
package com.spark.fixedlength

import java.util.{Locale, TimeZone}

import hydrograph.engine.core.constants.Constants
import hydrograph.engine.spark.helper.DelimitedAndFixedWidthHelper
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.slf4j.{Logger, LoggerFactory}


class DefaultSource extends RelationProvider
  with SchemaRelationProvider with CreatableRelationProvider with Serializable {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[DefaultSource])

  /**
    * Creates a new relation for data store in delimited given parameters.
    * Parameters have to include 'path' and optionally 'delimiter', 'quote', and 'header'
    */

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    createRelation(sqlContext, parameters, null)
  }

  /**
    * Creates a new relation for data store in delimited given parameters and user supported schema.
    * Parameters have to include 'path' and optionally 'delimiter', 'quote', and 'header'
    */

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): FixedLengthSchemeRelation = {

    val path = parameters.getOrElse("path", throw new RuntimeException("path option must be specified "))
    val inDateFormats: String = parameters.getOrElse("dateFormats", "null")
    val strict: Boolean = parameters.getOrElse("strict", "true").toBoolean
    val safe: Boolean = parameters.getOrElse("safe", "false").toBoolean
    val nullValue: String = parameters.getOrElse("nullValue", "")
    val quote: String = if (parameters.getOrElse("quote", "\"") == null) "\"" else parameters.getOrElse("quote", "\"")
    val treatEmptyValuesAsNulls: Boolean = parameters.getOrElse("treatEmptyValuesAsNulls", "false").toBoolean
    val charset: String = parameters.getOrElse("charset","UTF-8")
    val lengthsAndDelimiters = parameters.getOrElse("lengthsAndDelimiters", "")

    if (path == null || path.equals("")) {
      LOG.error("MixedScheme Input File path cannot be null or empty")
      throw new RuntimeException("MixedScheme Input File path cannot be null or empty")
    }

    val dateFormat: List[FastDateFormat] = getDateFormats(inDateFormats.split("\t").toList)

    FixedLengthSchemeRelation(
      Some(path),
      charset,
      quote,
      safe,
      strict,
      dateFormat,
      lengthsAndDelimiters,
      nullValue,
      treatEmptyValuesAsNulls,
      schema
    )(sqlContext)
  }


  private def fastDateFormat(dateFormat: String): FastDateFormat = if (!(dateFormat).equalsIgnoreCase("null")) {
      val date = FastDateFormat.getInstance(dateFormat,TimeZone.getDefault,Locale.getDefault)
//    val date = new FastDateFormat(dateFormat, Locale.getDefault)
//    date.setLenient(false)
//    date.setTimeZone(TimeZone.getDefault)
    date
  } else null

  private def getDateFormats(dateFormats: List[String]): List[FastDateFormat] = dateFormats.map{ e =>
    if (e.equals("null")){
      null
    } else {
      fastDateFormat(e)
    }
  }


  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {

    createRelation(sqlContext, parameters, data.schema)
  }

}
