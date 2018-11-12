package com.dzavorin.todo

import java.util.UUID

import com.dzavorin.todo.model._
import io.circe.generic.auto._
import io.finch.circe._
import io.finch._
import io.finch.syntax._
import io.finch.syntax.scalaFutures._
import com.dzavorin.todo.persistence.PersistenceService
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}

import scala.concurrent.{ExecutionContext, Future}

class TodoApi(persistence: PersistenceService)(implicit ec: ExecutionContext) {

  private[this] val version = path("v1")
  private[this] val todos = path("todos")
  private[this] val users = path("users")

  val getTodosEndpoint: Endpoint[Seq[TodoWithUser]] =
    get(todos) {
      persistence.findAllTodos.map(Ok)
    }

  val getTodoEndpoint: Endpoint[TodoWithUser] =
    get(todos :: path[UUID]) { id: UUID =>
      persistence findTodoById id map (_.toLeft(TodoNotFound(id)).fold(Ok, NotFound))
    }

  val createTodoEndpoint: Endpoint[Todo] =
    post(todos :: jsonBody[Todo]) { todo: Todo =>
      persistence saveTodo todo map Ok
    }

//  val updateTodo: Endpoint[TodoWithUser] = {
//    def copy(todo: Option[TodoWithUser], updatedTodo: Todo): Option[Todo] =
//      todo.map(_.copy( = updatedUser.fullname, jobTitle = updatedUser.jobTitle))
//
//    patch(todos :: path[UUID] :: jsonBody[Todo]) { (id: UUID, updatedTodo: Todo) =>
//      () => for {
//          todoWithUser <- persistence.findTodoById(id) map (_.map ( u => (u.user, updatedTodo.userId) match {
//            case (Some(user), Some(userId)) => updatedTodo.userId.toLeft().fold()
//            case (None) => copy(_, updatedTodo)
//          }))
//          todo <-
//          _ <- todo map (_ => Future.apply(persistence.saveTodo(_)))
//        } yield todo.toLeft(TodoNotFound(id)).fold(Ok, BadRequest)
//    }
//  }

  val getUsersEndpoint: Endpoint[Seq[User]] =
    get(users) {
      persistence.findAllUsers.map(Ok)
    }

  val getUserEndpoint: Endpoint[User] =
    get(users :: path[UUID]) { id: UUID =>
      persistence findUserById id map (_.toLeft(UserNotFound(id)).fold(Ok, NotFound))
    }

  val createUserEndpoint: Endpoint[User] =
    post(users :: jsonBody[User]) { user: User =>
      persistence saveUser user map Ok
    }

  val updateUser: Endpoint[User] = {
    def copy(user: Option[User], updatedUser: User): Option[User] =
      user.map(_.copy(fullname = updatedUser.fullname, jobTitle = updatedUser.jobTitle))

    patch(users :: path[UUID] :: jsonBody[User]) { (id: UUID, updatedUser: User) =>
      () => for {
          user <- persistence.findUserById(id) map (copy(_, updatedUser))
          _ <- user fold Future.unit (persistence.saveUser _)
        } yield user.toLeft(UserNotFound(id)).fold(Ok, BadRequest)
    }
  }

  private[this] val todoEndpoints = getTodosEndpoint :+: getTodoEndpoint :+: createTodoEndpoint //:+: updateTodo
  private[this] val userEndpoints = getUsersEndpoint :+: getUserEndpoint :+: createUserEndpoint :+: updateUser

  private[this] val opts: Endpoint[Unit] = options(*) {
    NoContent[Unit].withHeader(("Allow", "POST, GET, OPTIONS, DELETE, PATCH"))
  }

  private[this] val apiEndpoints = (version :: (todoEndpoints :+: userEndpoints :+: opts)).handle {
    case e: Exception => println(e.getMessage); BadRequest(e)
  }

  val service: Service[Request, Response] = apiEndpoints.toService
}
