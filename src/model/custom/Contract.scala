package model.custom

import app.Configs

case class Contract(
  team_key: String,
  player_key: String,
  seasonSigned: String, // year when contract was signed (i.e. first year of contract)
  contractLength: Int, // number of years (1-3)
  salaryByYear: Map[Int, Int] //only contains mappings for years in the contract
  ) {

  require(salaryByYear.keys.forall { season => season >= seasonSigned.toInt && season < seasonSigned.toInt + contractLength },
    s"Contract for team_key = '${team_key}' & player_key = '${player_key}' cannot contain salaries for seasons before " +
      s"$seasonSigned or after ${seasonSigned.toInt + contractLength - 1}")

  require(contractLength <= Configs.LeagueRules.maxContractLength,
    s"Contract for team_key = '${team_key}' & player_key = '${player_key}' must not exceed the league contract size limit of ${Configs.LeagueRules.maxContractLength} seasons")

}