package model

case class Managers(manager: Either[Manager, List[Manager]]) {
  val managers: List[Manager] = manager match {
    case Left(mgr)   => List(mgr)
    case Right(mgrs) => mgrs
  }
  
  override def toString: String = s"Managers(managers: " + managers.map(m => s"\n\t$m") + ")"
}

case class Manager(
  manager_id: String,
  nickname: String,
  guid: String,
  is_commissioner: Option[String],
  is_current_login: Option[String],
  is_comanager: Option[String],
  email: Option[String],
  image_url: Option[String])