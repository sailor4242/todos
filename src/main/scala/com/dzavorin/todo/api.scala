package com.dzavorin.todo

import java.util.UUID

import io.circe.generic.auto._
import io.finch.circe._
import io.finch._
import io.finch.syntax._
import io.finch.syntax.scalaFutures._
import com.dzavorin.todo.persistence.PersistenceService
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}

import scala.concurrent.ExecutionContext

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
      persistence.findTodoById(id).map(_.toLeft(TodoNotFound(id)).fold(Ok, NotFound))
  }

   val createTodoEndpoint: Endpoint[Todo] =
    post(todos :: jsonBody[Todo]) { todo: Todo =>
      persistence.saveTodo(todo)
        .map(Ok)
    }

   val getUsersEndpoint: Endpoint[Seq[User]] =
    get(users) {
      persistence.findAllUsers.map(Ok)
    }

   val getUserEndpoint: Endpoint[User] =
    get(users :: path[UUID]) { id: UUID =>
      persistence.findUserById(id).map(_.toLeft(UserNotFound(id)).fold(Ok, NotFound))
    }

   val createUserEndpoint: Endpoint[User] =
    post(users :: jsonBody[User]) { user: User =>
      persistence.saveUser(user)
        .map(Ok)
    }

  //
//  private[this] val getAccountEndpoint: Endpoint[Account] =
//    get("api" :: "account" :: path[UUID]) { (id: UUID) =>
//      mts.getAccount(id).toFuture.map {
//        case Some(a) => Ok(a)
//        case None => NotFound(AccountNotFound(id))
//      }
//    }
//
//  private[this] val transferMoneyEndpoint: Endpoint[Transaction] =
//    post("api" :: "transaction" :: jsonBody[Transaction]) { (tr: Transaction) =>
//      mts.transferMoney(tr).toFuture.map {
//        case Right(t) => Ok(t)
//        case Left(e) => e match {
//          case a@AccountNotFound(m) => NotFound(a)
//          case t@TransferError(m) => BadRequest(t)
//        }
//      }
//    }

  private[this] val todoEndpoints = getTodosEndpoint :+: getTodoEndpoint :+: createTodoEndpoint
  private[this] val userEndpoints =  getUsersEndpoint :+: getUserEndpoint :+: createUserEndpoint

//  val notFound: Endpoint[String] = * {
//    Output.payload("Service not found", Status.NotFound)
//  }

  private[this] val opts: Endpoint[Unit] = options(*) {
    NoContent[Unit].withHeader(("Allow", "POST, GET, OPTIONS, DELETE, PATCH"))
  }

  private[this] val apiEndpoints = (version :: (todoEndpoints :+: userEndpoints :+: opts)).handle {
    case e: Exception => println(e.getMessage) ; BadRequest(e)
  }

  val service: Service[Request, Response] = apiEndpoints.toService
}
