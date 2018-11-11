package com.dzavorin.todo

import java.util.UUID

case class TodoNotFound(id: UUID) extends Exception(s"Todo with id '$id' not found")

case class UserNotFound(id: UUID) extends Exception(s"User with id '$id' not found")
