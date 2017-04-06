package model

case class LeagueMatchupList(count: String, matchup: List[Matchup]) {
  override def toString: String = s"LeagueMatchupList(count: $count, matchup: " + matchup.map(m => s"\n\t$m") + ")"
}

case class Matchup(
  week: String,
  week_start: String,
  week_end: String,
  status: String,
  is_playoffs: String,
  is_consolation: String,
  is_matchup_recap_available: String,
  matchup_recap_url: String,
  matchup_grades: MatchupGradeList,
  is_tied: String,
  winner_team_key: String,
  teams: TeamList)

case class MatchupGradeList(matchup_grade: List[MatchupGrade]) {
  override def toString: String = s"MatchupGradeList(matchup_grade: " + matchup_grade.map(m => s"\n\t$m") + ")"
}

case class MatchupGrade(team_key: String, grade: String)