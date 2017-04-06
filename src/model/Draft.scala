package model

case class DraftResults(count: String, draft_result: List[DraftPick]) {
  override def toString: String = s"DraftResults(count: $count, draft_result: " + draft_result.map(m => s"\n\t$m") + ")"
}

case class DraftPick(pick: String, round: String, player_key: String, team_key: String, cost: Option[String])
