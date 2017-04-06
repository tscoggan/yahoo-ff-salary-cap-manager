package utils

import com.simpleyql.Api
import com.simpleyql.ApiFactory
import com.simpleyql.QueryResult
import java.util.Date
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import Cache.yqlQueries
import org.scala_tools.time.Imports._
import app.Configs

case class YQLQuery(query: String, response: String, lastRunTime: org.joda.time.DateTime) extends Serializable {
  val id: Int = YQLQuery.nextID
  
  override def toString: String = s"YQLQuery($query)"
}

object YQLQuery {
  private var idSequence: Int = 0

  def nextID: Int = {
    idSequence = idSequence + 1
    idSequence
  }
}

class YQLQueryUtil(conn: OAuthConnection) {
  val api: Api = ApiFactory.getApiInstance(conn.info.apiKey, conn.info.apiSecret, null, true, null)

  private val log = Logger.getLogger(this.getClass)

  val AUTHDATA_SEPARATOR = "&"

  def queryYQL(query: String): Option[String] = {
    try {
      if (conn.authorized) {
        log.info(s"Executing query: $query")
        val authdata = conn.accessToken.getToken + AUTHDATA_SEPARATOR + conn.accessToken.getSecret + AUTHDATA_SEPARATOR + conn.oauthSessionHandle

        // see if query results are cached before executing...
        yqlQueries.cache.find(_.query == query) match {
          case Some(cached) => ((cached.lastRunTime + Configs.Yahoo.queryCacheExpirationInHours.hour) < DateTime.now) match {
            case true =>
              log.info(s"Cached query result (as of ${cached.lastRunTime}) too old --> will remove from cache and re-run")
              yqlQueries.remove(cached)
              executeQuery(query, authdata)
            case false =>
              log.info(s"Re-using cached query result (as of ${cached.lastRunTime})")
              Some(cached.response)
          }
          case None =>
            executeQuery(query, authdata)
        }

      } else {
        log.error("Connection not authorized")
        None
      }
    } catch {
      case e: Exception =>
        log.error(s"Query failed: $query)", e)
        None
    }
  }

  private def executeQuery(query: String, authdata: String): Option[String] = {
    val result = YQLQuery(query, api.query(query, authdata).getText, DateTime.now)
    result.response.trim.length match {
      case 0 =>
        log.info("Query returned empty result")
        None
      case _ =>
        yqlQueries.add(result)
        log.info("Query result: " + result.response.trim)
        Some(result.response.trim)
    }
  }

}