package app

import utils._
import model._
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.json4s._
import org.json4s.jackson.JsonMethods._

object YahooAPI {

  private val log = Logger.getLogger(this.getClass)
  implicit val formats = DefaultFormats
  val conn = OAuthConnection.connect
  val yql = new YQLQueryUtil(conn)

  def getLeague(leagueKey: String = Configs.Yahoo.leagueKey): Option[League] = {
    val query = s"""select * from fantasysports.leagues where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league"
      //println("JSON: "+pretty(render(json)))
      json.extract[League]
    }
  }
  
  def getLeagueStandings(leagueKey: String = Configs.Yahoo.leagueKey): Option[LeagueStandings] = {
    val query = s"""select * from fantasysports.leagues.standings where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league" \ "standings"
      //println("JSON: "+pretty(render(json)))
      json.extract[LeagueStandings]
    }
  }
  
  def getLeagueScoreboard(leagueKey: String = Configs.Yahoo.leagueKey): Option[LeagueScoreboard] = {
    val query = s"""select * from fantasysports.leagues.scoreboard where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league" \ "scoreboard"
      //println("JSON: "+pretty(render(json)))
      json.extract[LeagueScoreboard]
    }
  }
  
  def getLeagueTransactions(leagueKey: String = Configs.Yahoo.leagueKey): Option[TransactionList] = {
    val query = s"""select * from fantasysports.leagues.transactions where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league" \ "transactions"
      //println("JSON: "+pretty(render(json)))
      json.extract[TransactionList]
    }
  }
  
  def getLeagueSettings(leagueKey: String = Configs.Yahoo.leagueKey): Option[LeagueSettings] = {
    val query = s"""select * from fantasysports.leagues.settings where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league" \ "settings"
      //println("JSON: "+pretty(render(json)))
      json.extract[LeagueSettings]
    }
  }
  
  def getDraftResults(leagueKey: String = Configs.Yahoo.leagueKey): Option[DraftResults] = {
    val query = s"""select * from fantasysports.draftresults where league_key='$leagueKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "league" \ "draft_results"
      //println("JSON: "+pretty(render(json)))
      json.extract[DraftResults]
    }
  }
  
  def getTeams(leagueKey: String = Configs.Yahoo.leagueKey): List[Team] = {
    getLeagueStandings(leagueKey) match {
      case Some(standings) => standings.teams.team
      case None => Nil
    }
  }
  
  def getTeam(teamKey: String): Option[Team] = {
    val query = s"""select * from fantasysports.teams where team_key='$teamKey'"""
    yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "team"
      //println("JSON: "+pretty(render(json)))
      json.extract[Team]
    }
  }

}