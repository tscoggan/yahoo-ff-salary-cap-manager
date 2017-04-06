package utils

import java.util.{ Date, Calendar }

object DateUtils {
 
  implicit class EnrichedDate(d: Date) {
    
    def toSqlTimestamp: java.sql.Timestamp = new java.sql.Timestamp(d.getTime)
    
  }

}