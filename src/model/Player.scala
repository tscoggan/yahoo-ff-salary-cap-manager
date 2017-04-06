package model

case class PlayerList(count: String, player: List[Player]) {
  override def toString: String = s"PlayerList(count: $count, player: " + player.map(m => s"\n\t$m") + ")"
}

case class Player(
  player_key: String,
  player_id: String,
  name: PlayerName,
  editorial_player_key: Option[String],
  editorial_team_key: Option[String],
  editorial_team_full_name: Option[String],
  editorial_team_abbr: String,
  bye_weeks: Option[ByeWeek],
  uniform_number: Option[String],
  display_position: String,
  headshot: Option[Headshot],
  image_url: Option[String],
  is_undroppable: Option[String],
  position_type: String,
  eligible_positions: Option[EligiblePositions],
  has_player_notes: Option[String],
  selected_position: Option[SelectedPosition],
  is_editable: Option[String],
  transaction_data: Option[PlayerTransaction])

case class PlayerName(
  full: String,
  first: String,
  last: Option[String],
  ascii_first: String,
  ascii_last: Option[String]) {
  override def toString: String = full
}

case class EligiblePositions(position: Either[String, List[String]])

case class SelectedPosition(coverage_type: String, week: String, position: String)

case class ByeWeek(week: String)

case class Headshot(url: String, size: String)

case class PlayerTransaction(
  `type`: String,
  source_type: String,
  source_team_key: Option[String],
  source_team_name: Option[String],
  destination_type: String)