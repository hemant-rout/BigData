
package com.spark.fixedlength

import java.math.BigDecimal
import java.sql.{Date, Timestamp}
import java.text.NumberFormat
import java.util.Locale

import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.sql.types.{DateType, StringType, _}
import org.json4s.ParserUtil.ParseException

import scala.util.Try

object TypeCast {


@throws(classOf[Exception])
  def inputValue(value: String, castType: DataType, nullable: Boolean = true, nullValue:String, treatEmptyValuesAsNulls:Boolean=true, dateFormat: FastDateFormat) : Any= {

     if (value == nullValue && nullable || (value == nullValue && treatEmptyValuesAsNulls)) {
        null
      } else {
        castType match {
          case _: StringType => value
          case _: IntegerType => value.toInt
          case _: LongType => value.toLong
          case _: ByteType => value.toByte
          case _: ShortType => value.toShort
          case _: DecimalType => new BigDecimal(value.replaceAll(",",""))
          case _: DateType =>  {
        	  try{
        		  new Date(dateFormat.parse(value).getTime)
        	  }catch{
        	  case e:Exception => {
        		  throw new RuntimeException(", Error being -> ",e)
        	  }
        	  }
          }
          case _: FloatType => Try(value.toFloat)
            .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(value).floatValue())
          case _: DoubleType => Try(value.toDouble)
            .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(value).doubleValue())
          case _: BooleanType => value.toBoolean
          case _: TimestampType  => {
            try{
              new Timestamp(dateFormat.parse(value).getTime)
            }catch{
              case e:ParseException => throw new RuntimeException(", Error being -> ",e)
            }
          }
          case _ => throw new RuntimeException(s"Unsupported type: ${castType.typeName}")
        }
    }
  }

  def outputValue(value: Any, castType: DataType, dateFormat: FastDateFormat) : AnyRef= {

    castType match {
      case _: TimestampType => if (value == null) "" else if (dateFormat != null && !dateFormat.equals("null")) dateFormat.format(new Date(value.asInstanceOf[Timestamp].getTime)) else value.toString
      case _: DateType => if (value == null) "" else if (dateFormat != null && !dateFormat.equals("null")) dateFormat.format(value) else value.toString
      case _ => value.asInstanceOf[AnyRef]
    }
  }
}
