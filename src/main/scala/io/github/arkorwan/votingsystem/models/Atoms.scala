package io.github.arkorwan.votingsystem.models

case class Party(id: Int, name: String = "")

case class District(id: Int) extends AnyVal

case class Votes(value: Int) extends AnyVal {
  def +(other: Votes) = Votes(value + other.value)

  def -(other: Votes) = Votes(value - other.value)
}

case class Seats(value: Int) extends AnyVal {
  def +(other: Seats) = Seats(value + other.value)

  def -(other: Seats) = Seats(value - other.value)
}

case class Quota(value: Double) extends AnyVal {
  def +(other: Quota) = Quota(value + other.value)

  def -(other: Quota) = Quota(value - other.value)
}


