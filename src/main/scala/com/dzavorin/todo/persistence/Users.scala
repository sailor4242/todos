package com.dzavorin.todo.persistence

import java.util.UUID

import com.dzavorin.todo.model.User
import com.dzavorin.todo.persistence.Teams._
import com.dzavorin.todo.persistence.Users.users
import slick.jdbc.H2Profile.api._

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[UUID]("ID", O.PrimaryKey, O.Length(16))
  def fullname = column[String]("FULLNAME", O.Length(64))
  def jobTitle = column[String]("JOBTITLE", O.Length(64))
  def teamId = column[Option[UUID]]("USERID")

  def user = foreignKey("TEAM_FK", teamId, teams)(_.id.?, onUpdate = ForeignKeyAction.Restrict)

  override def * = (id, fullname, jobTitle, teamId) <> ((User.apply _).tupled, User.unapply)
}

object Users {
  val users = TableQuery[Users]
}

trait UsersQueries extends ImplicitDatabase {

  def findAllUsers = {
    val query = for { user <- users } yield user
    db.run(query.result)
  }

  def findUserById(id: UUID) = {
    val query = for (user <- users.filter { _.id === id} ) yield user
    db.run(query.result.headOption)
  }

  def saveUser(user: User) = {
    db.run(users += user) map {_ => user}
  }

}
