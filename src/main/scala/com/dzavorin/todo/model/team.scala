package com.dzavorin.todo.model

import java.util.UUID

import io.circe.generic.JsonCodec

@JsonCodec case class Team(id: UUID, title: String)
