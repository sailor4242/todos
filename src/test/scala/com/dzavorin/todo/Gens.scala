package com.dzavorin.todo

import org.joda.time.LocalDate
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait Gens {

  val localDateGen: Gen[LocalDate] = Gen.calendar map LocalDate.fromCalendarFields

  implicit val localDateArb: Arbitrary[LocalDate] = Arbitrary(localDateGen)

  val stringShorterThan = (n: Int) => Gen.alphaStr.suchThat(_.length <= n)
  val string16 = stringShorterThan(16)
  val string64 = stringShorterThan(64)
  val string512 = stringShorterThan(512)

  val genTodo: Gen[Todo] = for {
    id <- Gen.uuid
    title <- string512
    status <- string16
    url <- string64
    created <- localDateGen
  } yield Todo(id, title, status, url, created)

  implicit val arbTodo: Arbitrary[Todo] = Arbitrary(genTodo)

  val genUser: Gen[User] = for {
    id <- Gen.uuid
    fullname <- string64
    jobTitle <- string64
  } yield User(id, fullname, jobTitle)

  implicit val arbUser: Arbitrary[User] = Arbitrary(genUser)

}
