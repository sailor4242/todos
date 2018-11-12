package com.dzavorin.todo.model

import java.util.UUID

import io.circe.generic.JsonCodec

abstract sealed class UserBase(id: UUID,
                               fullname: String,
                               jobTitle: String)

@JsonCodec case class User(id: UUID,
                      fullname: String,
                      jobTitle: String,
                      teamId: Option[UUID]) extends UserBase(id, fullname, jobTitle)

@JsonCodec case class UserWithTeam(id: UUID,
                        fullname: String,
                        jobTitle: String,
                        team: Option[Team]) extends UserBase(id, fullname, jobTitle)
