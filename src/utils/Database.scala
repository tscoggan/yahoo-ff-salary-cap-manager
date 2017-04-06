package utils

import java.sql._
import org.h2.jdbcx.JdbcConnectionPool
import app.Configs
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import model._
import model.custom._
import utils.DateUtils._
import utils.CommonUtils._
import java.util.{ Calendar, Date }
import scala.annotation.tailrec
import scala.collection.mutable

object Database {

  private val log = Logger.getLogger(this.getClass)

  private val connectionPool: JdbcConnectionPool = JdbcConnectionPool.create(Configs.Database.url, Configs.Database.user, Configs.Database.password)

  def getConnection: Connection = connectionPool.getConnection

  def createDatabase = {
    log.info("Creating database...")
    val db = getConnection
    val stmt = db.createStatement

    ignoreErrors(stmt.executeUpdate(s"drop schema ${Configs.Database.schema}"))
    stmt.executeUpdate(s"create schema ${Configs.Database.schema}")

    stmt.executeUpdate(s"""
      create table ${Configs.Database.schema}.CONTRACTS(
        CONTRACT_ID identity primary key,
        ROW_INSERT_TIME timestamp not null,
        ROW_DELETE_TIME timestamp,
        TEAM_KEY varchar(20) not null,
        PLAYER_KEY varchar(20) not null,
        SEASON_SIGNED varchar(4) not null,
        CONTRACT_LENGTH integer,
        ${(1 to Configs.LeagueRules.maxContractLength).map(i => s"SEASON_${i}_VALUE integer").mkString(", ")})      
    """)

    db.close
    log.info("Done creating database!")
  }

  def insert(c: Contract) = {
    val db = getConnection

    val s1 = db.prepareStatement(s"""
      update ${Configs.Database.schema}.CONTRACTS
      set ROW_DELETE_TIME = ?
      where PLAYER_KEY = ? and ROW_DELETE_TIME is null      
    """)
    s1.setTimestamp(1, Calendar.getInstance.getTime.toSqlTimestamp)
    s1.setString(2, c.player_key)
    s1.execute
    s1.close

    val s2 = db.prepareStatement(s"""
      insert into ${Configs.Database.schema}.CONTRACTS
        (TEAM_KEY, PLAYER_KEY, SEASON_SIGNED, CONTRACT_LENGTH, ROW_INSERT_TIME, 
        ${(1 to Configs.LeagueRules.maxContractLength).map(i => s"SEASON_${i}_VALUE").mkString(", ")})
      values (?, ?, ?, ?, ${(1 to Configs.LeagueRules.maxContractLength).map(i => "?").mkString(", ")}, ?)      
    """)
    s2.setString(1, c.team_key)
    s2.setString(2, c.player_key)
    s2.setString(3, c.seasonSigned)
    s2.setInt(4, c.contractLength)
    s2.setTimestamp(5, Calendar.getInstance.getTime.toSqlTimestamp)
    (1 to Configs.LeagueRules.maxContractLength).foreach { i =>
      c.salaryByYear.get(c.seasonSigned.toInt + (i - 1)) match {
        case Some(salary) => s2.setInt(5 + i, salary)
        case None         => s2.setNull(5 + i, java.sql.Types.INTEGER)
      }
    }
    s2.execute
    s2.close

    db.close
  }

  def getContracts(t: Team): List[Contract] = getContracts(t.team_key)

  def getContracts(team_key: String): List[Contract] = {
    val db = getConnection

    val sql = s"""
      select TEAM_KEY, PLAYER_KEY, SEASON_SIGNED, CONTRACT_LENGTH, ${(1 to Configs.LeagueRules.maxContractLength).map(i => s"SEASON_${i}_VALUE").mkString(", ")}
      from ${Configs.Database.schema}.CONTRACTS
      where TEAM_KEY = '$team_key' and ROW_DELETE_TIME is null
    """

    val s = db.createStatement
    val rs = s.executeQuery(sql)
    val results = parse(rs).map(row =>
      Contract(
        row.get("TEAM_KEY").get,
        row.get("PLAYER_KEY").get,
        row.get("SEASON_SIGNED").get,
        row.get("CONTRACT_LENGTH").map(_.toInt).getOrElse(0),
        (1 to Configs.LeagueRules.maxContractLength).map { i =>
          (row.get("SEASON_SIGNED").get.toInt + (i - 1), row.get(s"SEASON_${i}_VALUE").map(_.toInt))
        }.collect { case (season, salary) if salary.nonEmpty => (season, salary.get) }.toMap))

    s.close
    db.close
    results
  }

  private def parse(rs: ResultSet): List[Map[String, String]] = {
    val columnNames = (1 to rs.getMetaData.getColumnCount).toList.map(rs.getMetaData.getColumnLabel(_))

    @tailrec def parseNextRow(rs: ResultSet, result: mutable.ListBuffer[Map[String, String]] = mutable.ListBuffer.empty): List[Map[String, String]] = {
      rs.next match {
        case true =>
          result += columnNames.map(cn => (cn, rs.getString(cn))).toMap.filterNot { case (k, v) => v == null }
          parseNextRow(rs, result)
        case false => result.toList
      }
    }

    parseNextRow(rs)
  }

}