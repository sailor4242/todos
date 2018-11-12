package com.dzavorin.todo.persistence

import java.util.UUID

import com.dzavorin.todo.model.Team
import slick.jdbc.H2Profile.api._

class Teams(tag: Tag) extends Table[Team](tag, "TEAMS") {
  def id = column[UUID]("ID", O.PrimaryKey, O.Length(16))
  def title = column[String]("TITLE", O.Length(512))

  def * = (id, title) <> ((Team.apply _).tupled, Team.unapply)
}

object Teams {
  val teams = TableQuery[Teams]
}

trait TeamsQueries extends ImplicitDatabase {

}