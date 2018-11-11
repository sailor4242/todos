package com.dzavorin.todo

import com.dzavorin.todo.persistence.PersistenceService
import io.finch.{Application, Input}
import utest._
import org.scalacheck.Prop.forAll
import io.finch.circe._

import scala.concurrent.ExecutionContext.Implicits.global

object TodosEndpointsTests extends TestSuite with UTestScalaCheck with Gens {

  val persistence = new PersistenceService
  persistence.createSchema()
  val api = new TodoApi(persistence)
  val todosRoot = "/todos"
  val usersRoot = "/users"

  override def utestAfterEach(path: Seq[String]): Unit = {
    persistence.truncate()
  }

  val tests = Tests {

    //    "Get all todos" - {
    //      forAll { todos: List[Todo] =>
    //        todos.foreach(persistence.saveTodo)
    //        val input = Input.get(todosRoot)
    //        val response = api.getTodosEndpoint(input).awaitValueUnsafe()
    //        response.get.map(_.title) == todos.map(_.title)
    //      }.checkUTest()
    //    }

    "Get todo by id" - {
      forAll { todo: Todo =>
        persistence.saveTodo(todo)
        val input = Input.get(s"$todosRoot/${todo.id}")
        val response = api.getTodoEndpoint(input).awaitValueUnsafe()
        val todoWithUser = TodoWithUser(todo.id, todo.title, todo.status, todo.url, todo.created, None)
        response.get == todoWithUser
      }.checkUTest()
    }

    "Save new todo" - {
      forAll { todo: Todo =>
        val input = Input.post(todosRoot).withBody[Application.Json](todo)
        val response = api.createTodoEndpoint(input).awaitValueUnsafe()
        response.get == todo
      }.checkUTest()
    }

    //    "Get all users" - {
    //      forAll { users: List[User] =>
    //        users.foreach(persistence.saveUser)
    //        val input = Input.get(usersRoot)
    //        val response = api.getUsersEndpoint(input).awaitValueUnsafe()
    //        response.get == users
    //      }.checkUTest()
    //    }

    "Get user by id" - {
      forAll { user: User =>
        persistence.saveUser(user)
        val input = Input.get(s"$usersRoot/${user.id}")
        val response = api.getUserEndpoint(input).awaitValueUnsafe()
        response.get == user
      }.checkUTest()
    }

    "Save new user" - {
      forAll { user: User =>
        val input = Input.post(usersRoot).withBody[Application.Json](user)
        val response = api.createUserEndpoint(input).awaitValueUnsafe()
        response.get == user
      }.checkUTest()
    }
  }
}
