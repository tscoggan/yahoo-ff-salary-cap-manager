package model

import app.YahooAPI
import model.custom._
import utils.Database
import org.json4s._
import org.json4s.jackson.JsonMethods._

case class TeamList(count: String, team: List[Team]) {
  override def toString: String = s"TeamList(count: $count, team: " + team.map(m => s"\n\t$m") + ")"
}

case class Team(
  team_key: String,
  team_id: String,
  name: String,
  is_owned_by_current_login: Option[String],
  url: String,
  team_logos: TeamLogos,
  waiver_priority: String,
  faab_balance: Option[String],
  division_id: Option[String],
  number_of_moves: String,
  number_of_trades: String,
  roster_adds: RosterAdds,
  clinched_playoffs: Option[String],
  league_scoring_type: String,
  draft_position: String,
  has_draft_grade: String,
  draft_grade: Option[String],
  draft_recap_url: Option[String],
  managers: Managers,
  team_standings: Option[TeamStandings],
  team_points: Option[TeamPoints],
  team_projected_points: Option[TeamPoints]) {

  lazy val players: List[Player] = {
    val query = s"""select * from fantasysports.teams.roster where team_key='$team_key'"""
    implicit val formats = DefaultFormats
    YahooAPI.yql.queryYQL(query).map { result =>
      val json = parse(result) \ "query" \ "results" \ "team" \ "roster" \ "players" \ "player"
      //println("JSON: "+pretty(render(json)))
      json.extract[List[Player]]
    }.getOrElse(Nil)
  }

  lazy val contracts: List[Contract] = Database.getContracts(this)

  lazy val totalSalaryByYear: Map[Int, Int] = {
    val years = contracts.flatMap(_.salaryByYear.keys).distinct.sorted
    years.map { year => (year, contracts.map(_.salaryByYear.getOrElse(year, 0)).sum) }.toMap
  }
}

case class TeamStandings(
  rank: String,
  playoff_seed: String,
  outcome_totals: OutcomeTotals,
  streak: Streak,
  points_for: String,
  points_against: String)

case class OutcomeTotals(wins: String, losses: String, ties: String, percentage: String)

case class Streak(`type`: String, value: String)

case class TeamLogos(team_logo: TeamLogo)

case class TeamLogo(size: String, url: String)

case class TeamPoints(coverage_type: String, week: Option[String], season: Option[String], total: String)

case class RosterAdds(coverage_type: String, coverage_value: String, value: String)