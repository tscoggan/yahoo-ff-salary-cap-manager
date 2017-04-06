package model

case class TransactionList(count: String, transaction: List[Transaction]) {
  override def toString: String = s"TransactionList(count: $count, transaction: " + transaction.map(m => s"\n\t$m") + ")"
}

case class Transaction(
  transaction_key: String,
  transaction_id: String,
  `type`: String,
  status: String,
  timestamp: String,
  faab_bid: Option[String],
  players: Option[PlayerList])