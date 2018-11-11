package com.dzavorin.todo.persistence

import java.util.UUID

import com.dzavorin.todo.User
import slick.jdbc.H2Profile.api._

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[UUID]("ID", O.PrimaryKey, O.Length(16))
  def fullname = column[String]("FULLNAME", O.Length(64))
  def jobTitle = column[String]("JOBTITLE", O.Length(64))

  override def * = (id, fullname, jobTitle) <> ((User.apply _).tupled, User.unapply)
}

object Users {
  val users = TableQuery[Users]
}
