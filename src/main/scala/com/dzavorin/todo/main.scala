package com.dzavorin.todo

import com.dzavorin.todo.persistence.PersistenceService
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await

object Main extends Config with Filters {
  import scala.concurrent.ExecutionContext.Implicits.global

  val persistence = new PersistenceService
  persistence.createSchema() onComplete (_ => persistence.createDataset())

  val api: Service[Request, Response] =
    corsFilter.andThen(new TodoApi(persistence).service)

  def main(args: Array[String]): Unit = {
    println(s"Setting up API server with host=$host, port=$port, externalUrl=$externalUrl...")
    val server = Http.server.serve(internalUrl, api)

    Await.ready(server)
  }
}
