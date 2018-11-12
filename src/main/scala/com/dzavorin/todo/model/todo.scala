package com.dzavorin.todo.model

import java.time.format.DateTimeParseException
import java.util.UUID

import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.joda.time.LocalDate

abstract sealed class TodoBase(id: UUID,
                               title: String,
                               status: String,
                               url: String,
                               created: LocalDate)

case class Todo(id: UUID,
                title: String,
                status: String,
                url: String,
                created: LocalDate,
                userId: Option[UUID] = None) extends TodoBase(id, title, status, url, created)

case class TodoWithUser(id: UUID,
                        title: String,
                        status: String,
                        url: String,
                        created: LocalDate,
                        user: Option[User]) extends TodoBase(id, title, status, url, created)

object Todo extends LocalDateCirce {
  implicit lazy val todoDecoder: Decoder[Todo] = deriveDecoder
  implicit lazy val todoEncoder: Encoder[Todo] = deriveEncoder
}

object TodoWithUser extends LocalDateCirce {
  implicit lazy val todoWithUserDecoder: Decoder[TodoWithUser] = deriveDecoder
  implicit lazy val todoWithUserEncoder: Encoder[TodoWithUser] = deriveEncoder
}

trait LocalDateCirce {
  implicit final val decodeDuration: Decoder[LocalDate] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) => try Right(LocalDate.parse(s)) catch {
          case _: DateTimeParseException => Left(DecodingFailure("Duration", c.history))
        }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[LocalDate]]
      }
    }

  implicit final val encodeDuration: Encoder[LocalDate] =
    Encoder.instance(duration => Json.fromString(duration.toString))
}