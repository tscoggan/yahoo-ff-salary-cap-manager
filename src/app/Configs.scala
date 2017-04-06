package app

import com.typesafe.config.{ ConfigFactory, Config }
import scala.collection.JavaConversions._

object Configs {
  private val conf = ConfigFactory.load

  object Yahoo {
    private val conf = Configs.conf.getConfig("yahoo")

    val apiKey = conf.getString("api_key")
    val apiSecret = conf.getString("api_secret")
    val leagueKey = conf.getString("league_key")
    val queryCacheExpirationInHours = conf.getInt("query_cache_expiration_in_hours")

    val historicalLeagueKeys: Map[Int, String] = conf.getConfigList("historical_league_keys").toList.map { cfg =>
      (cfg.getInt("season"), cfg.getString("league_key"))
    }.toMap
  }
  
  object Database {
    private val conf = Configs.conf.getConfig("database")
    
    val url = conf.getString("url")
    val user = conf.getString("user")
    val password = conf.getString("password")
    val schema = conf.getString("schema")
  }
  
  object LeagueRules {
    private val conf = Configs.conf.getConfig("league_rules")
    
    val maxContractLength = conf.getInt("max_contract_length")
  }

}

object ConfigTest extends App {
  println("apiKey: " + Configs.Yahoo.apiKey)
  println("apiSecret: " + Configs.Yahoo.apiSecret)
  println("leagueKey: " + Configs.Yahoo.leagueKey)
  println("historicalLeagueKeys: " + Configs.Yahoo.historicalLeagueKeys)
}