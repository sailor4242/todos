package com.dzavorin.todo.persistence

import org.joda.time.LocalDate
import java.util.UUID

import com.dzavorin.todo.model._
import slick.jdbc.H2Profile.api._
import com.dzavorin.todo.persistence.Todos._
import com.dzavorin.todo.persistence.Users._
import com.dzavorin.todo.persistence.Teams._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

trait ImplicitDatabase {
  protected[this] implicit def db: Database
  protected[this] implicit def ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}

class PersistenceService extends TodosQueries
                            with UsersQueries
                            with TeamsQueries {

  implicit lazy val db = Database.forConfig("db")

  def createSchema() = db.run(
    DBIO.seq((
      users.schema ++ todos.schema ++ teams.schema
      ).create
    ))

  def truncate() = db.run(
    DBIO.seq(
      todos.delete,
      users.delete,
      teams.delete
    ))

  def createDataset() = Await.result({
    val userOneId = UUID.randomUUID()
    val userTwoId = UUID.randomUUID()
    val userThreeId = UUID.randomUUID()

    db.run(
      DBIO.seq(
        users ++= Seq(
          new User(userOneId, "Alex", "Software Engineer", None),
          new User(userTwoId, "Konstantin", "Team Lead", None),
          new User(userThreeId, "Pavel", "Scrum Master", None)
        ),

        todos ++= Seq(
          Todo(UUID.randomUUID(), "Read Concurrent Programming in Scala", "In progress", " ", new LocalDate(2018, 11, 25), Some(userOneId)),
          Todo(UUID.randomUUID(), "Use more monads", "In progress", " ", new LocalDate(2018, 6, 11), Some(userTwoId)),
          Todo(UUID.randomUUID(), "Watch FP to the MAX", "Ready", " ", new LocalDate(2018, 4, 13), Some(userThreeId))
        )
      ))
  }, Duration.Inf)

}