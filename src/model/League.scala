package model

case class League(
  //id: Int,
  league_key: String,
  league_id: String,
  //league_chat_id: String,
  name: String,
  url: String,
  password: String,
  draft_status: String,
  num_teams: String,
  edit_key: String,
  weekly_deadline: String,
  league_update_timestamp: String,
  scoring_type: String,
  league_type: String,
  renew: String,
  renewed: String,
  short_invitation_url: String,
  allow_add_to_dl_extra_pos: String,
  is_pro_league: String,
  is_cash_league: String,
  current_week: String,
  start_week: String,
  start_date: String,
  end_week: String,
  end_date: String,
  is_finished: String,
  game_code: String,
  season: String)

case class LeagueStandings(teams: TeamList)

case class LeagueScoreboard(week: String, matchups: LeagueMatchupList)

case class LeagueSettings(
  draft_type: String,
  is_auction_draft: String,
  scoring_type: String,
  persistent_url: Option[String],
  uses_playoff: String,
  has_playoff_consolation_games: Option[String],
  playoff_start_week: Option[String],
  uses_playoff_reseeding: Option[String],
  uses_lock_eliminated_teams: Option[String],
  num_playoff_teams: Option[String],
  num_playoff_consolation_teams: Option[String],
  has_multiweek_championship: Option[String],
  waiver_type: String,
  waiver_rule: String,
  uses_faab: String,
  draft_time: String,
  draft_pick_time: Option[String],
  post_draft_players: String,
  max_teams: String,
  waiver_time: String,
  trade_end_date: String,
  trade_ratify_type: String,
  trade_reject_time: String,
  player_pool: String,
  cant_cut_list: String,
  is_publicly_viewable: Option[String],
  roster_positions: LeagueRosterPositionList,
  //divisions: Option[LeagueDivisionList], Example???
  stat_categories: LeagueStatCategories,
  stat_modifiers: Option[LeagueStatModifiers],
  season_type: Option[String],
  max_games_played: Option[String],
  //can_trade_draft_picks: String,
  //uses_roster_import: String,
  //roster_import_deadline: String,
  pickem_enabled: String,
  uses_fractional_points: String,
  uses_negative_points: String)

case class LeagueRosterPositionList(roster_position: List[RosterPosition]) {
  override def toString: String = s"LeagueRosterPositionList(roster_position: " + roster_position.map(m => s"\n\t$m") + ")"
}

case class RosterPosition(position: String, position_type: Option[String], count: String)

case class LeagueStatCategories(stats: LeagueStatList)

case class LeagueStatList(stat: List[Stat]) {
  override def toString: String = s"LeagueStatList(stat: " + stat.map(m => s"\n\t$m") + ")"
}

case class Stat(stat_id: String, enabled: String, name: String, display_name: String, sort_order: String, position_type: String,
                stat_position_types: StatPositionTypeList)

case class LeagueStatModifiers(stats: StatModifierList)

case class StatModifierList(stat: List[StatModifier]) {
  override def toString: String = s"StatModifierList(stat: " + stat.map(m => s"\n\t$m") + ")"
}

case class StatModifier(stat_id: String, value: String)

case class StatPositionTypeList(stat_position_type: StatPositionType)

case class StatPositionType(position_type: String)