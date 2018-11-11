package com.dzavorin.todo.persistence

import org.joda.time.LocalDate
import java.util.UUID

import com.dzavorin.todo.{Todo, TodoWithUser, User}
import slick.jdbc.H2Profile.api._
import com.dzavorin.todo.persistence.Todos._
import com.dzavorin.todo.persistence.Users._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PersistenceService {
  import scala.concurrent.ExecutionContext.Implicits.global
  lazy val db = Database.forConfig("db")

  def createSchema() = db.run(
    DBIO.seq((
      users.schema ++
        todos.schema
      ).create
    ))

  def truncate() = db.run(
    DBIO.seq(
      todos.delete,
      users.delete
    ))

  def createDataset() = Await.result({
    val userOneId = UUID.randomUUID()
    val userTwoId = UUID.randomUUID()
    val userThreeId = UUID.randomUUID()

    db.run(
      DBIO.seq(
        users ++= Seq(
          new User(userOneId, "Alex", "Software Engineer"),
          new User(userTwoId, "Konstantin", "Team Lead"),
          new User(userThreeId, "Pavel", "Scrum Master")
        ),

        todos ++= Seq(
          Todo(UUID.randomUUID(), "Read Concurrent Programming in Scala", "In progress", " ", new LocalDate(2018, 11, 25), Some(userOneId)),
          Todo(UUID.randomUUID(), "Use more monads", "In progress", " ", new LocalDate(2018, 6, 11), Some(userTwoId)),
          Todo(UUID.randomUUID(), "Watch FP to the MAX", "Ready", " ", new LocalDate(2018, 4, 13), Some(userThreeId))
        )
      ))
  }, Duration.Inf)

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