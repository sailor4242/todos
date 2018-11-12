package com.dzavorin.todo.persistence

import java.util.UUID

import com.dzavorin.todo.model.{Todo, TodoWithUser}
import com.dzavorin.todo.persistence.Todos.todos
import org.joda.time.LocalDate
import com.github.tototoshi.slick.H2JodaSupport._
import slick.jdbc.H2Profile.api._
import com.dzavorin.todo.persistence.Users._

class Todos(tag: Tag) extends Table[Todo](tag, "TODOS") {
  def id = column[UUID]("ID", O.PrimaryKey, O.Length(16))
  def title = column[String]("TITLE", O.Length(512))
  def status = column[String]("STATUS", O.Length(16))
  def url = column[String]("URL", O.Length(64))
  def created = column[LocalDate]("CREATED")
  def userId = column[Option[UUID]]("USER_ID")

  def user = foreignKey("USER_FK", userId, users)(_.id.?, onUpdate = ForeignKeyAction.Restrict)

  def * = (id, title, status, url, created, userId) <> ((Todo.apply _).tupled, Todo.unapply)
}

object Todos {
  val todos = TableQuery[Todos]
}

trait TodosQueries extends ImplicitDatabase {

  def findAllTodos = {
    val query = for {(todo, user) <- todos joinLeft users on (_.userId === _.id)} yield (todo, user)
    db.run(query.result)
      .map(_.map { case (t, u) => TodoWithUser(t.id, t.title, t.status, t.url, t.created, u)})
  }

  def findTodoById(id: UUID) = {
    val query = for {(todo, user) <- todos.filter { _.id === id} joinLeft users on (_.userId === _.id)} yield (todo, user)
    db.run(query.result.headOption)
      .map(_.map { case (t, u) => TodoWithUser(t.id, t.title, t.status, t.url, t.created, u)})
  }

  def saveTodo(todo: Todo) = {
    db.run(todos += todo) map {_ => todo}
  }

}
