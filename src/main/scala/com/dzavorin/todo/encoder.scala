package com.dzavorin.todo

import io.circe.{Encoder, Json}

package object encoder {

  implicit final val encodeExceptionCirce: Encoder[Exception] = Encoder.instance(e =>
    Json.obj("message" -> Option(e.getMessage).fold(Json.Null)(Json.fromString))
  )

}
