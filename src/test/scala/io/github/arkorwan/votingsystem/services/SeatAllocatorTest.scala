package io.github.arkorwan.votingsystem.services

import io.github.arkorwan.votingsystem.models._
import org.scalatest.{FunSuite, Matchers}

class SeatAllocatorTest extends FunSuite with Matchers {

  val asSeats = (kv: (Int, Int)) => (Party(kv._1), Seats(kv._2))
  val asVotes = (kv: (Int, Int)) => (Party(kv._1), Votes(kv._2))

  test("2 parties, A wins all districts but underachieves, gets bumped up to the expected") {

    // party 1 got 60% of votes but 40% of the seats
    // party 2 got 40% of votes but 0% of the seats
    val voteResults = (0 until 12).map(i => DistrictResult(District(i), Map(1 -> 60, 2 -> 40).map(asVotes)))
    val allocator = new SeatAllocator(Seats(18))

    // ratio should be 60 / 40
    allocator.allocate(voteResults) shouldBe Map(1 -> 18, 2 -> 12).map(asSeats)

  }

  test("2 parties, A wins all districts but overachieves, gets no extra seats") {

    // party 1 got 60% of votes but 75% of the seats
    // party 2 got 40% of votes but 0% of the seats
    val voteResults = (0 until 12).map(i => DistrictResult(District(i), Map(1 -> 60, 2 -> 40).map(asVotes)))
    val allocator = new SeatAllocator(Seats(4))

    // all extra seats go to 2
    allocator.allocate(voteResults) shouldBe Map(1 -> 12, 2 -> 4).map(asSeats)

  }

  test("3 parties, one overachieves, seats got divided proportionally to the other two") {

    // party 1 got 40% of votes but 60% of the seats
    // party 2 got 35% of votes but 0% of the seats
    // party 3 got 25% of votes but 0% of the seats

    val voteResults = (0 until 18).map(i => DistrictResult(District(i), Map(1 -> 40, 2 -> 35, 3 -> 25).map(asVotes)))
    val allocator = new SeatAllocator(Seats(12))

    // all extra seats are divided proportionally (7:5) to 2 and 3
    allocator.allocate(voteResults) shouldBe Map(1 -> 18, 2 -> 7, 3 -> 5).map(asSeats)

  }

  test("5 parties, all underachieve, fractional seats are assigned correctly") {

    // Each party wins 2 district seats
    val voteResults = Seq(
      DistrictResult(District(0), Map(1 -> 40, 2 -> 30, 3 -> 30).map(asVotes)),
      DistrictResult(District(1), Map(1 -> 40, 2 -> 30, 4 -> 30).map(asVotes)),
      DistrictResult(District(2), Map(1 -> 30, 2 -> 40, 5 -> 30).map(asVotes)),
      DistrictResult(District(3), Map(2 -> 40, 3 -> 30, 4 -> 30).map(asVotes)),
      DistrictResult(District(4), Map(2 -> 30, 3 -> 40, 5 -> 30).map(asVotes)),
      DistrictResult(District(5), Map(3 -> 40, 4 -> 30, 5 -> 30).map(asVotes)),
      DistrictResult(District(6), Map(1 -> 10, 2 -> 20, 3 -> 30, 4 -> 40).map(asVotes)),
      DistrictResult(District(7), Map(1 -> 10, 2 -> 10, 3 -> 30, 5 -> 50).map(asVotes)),
      DistrictResult(District(8), Map(1 -> 20, 3 -> 20, 4 -> 40, 5 -> 20).map(asVotes)),
      DistrictResult(District(9), Map(2 -> 10, 3 -> 10, 4 -> 20, 5 -> 60).map(asVotes))
    )
    val allocator = new SeatAllocator(Seats(12))
    // party 1: 150 votes -> 15% -> 3.30
    // party 2: 210 votes -> 21% -> 4.62
    // party 3: 230 votes -> 23% -> 5.06
    // party 4: 190 votes -> 19% -> 4.18
    // party 5: 220 votes -> 22% -> 4.84

    // fractional seats (2) should go to parties 5 (.84) and 2 (.62)

    allocator.allocate(voteResults) shouldBe Map(1 -> 3, 2 -> 5, 3 -> 5, 4 -> 4, 5 -> 5).map(asSeats)

  }

  // examples from http://library2.parliament.go.th/ejournal/content_af/2559/mar2559-2.pdf, page 8

  test("parliament example 1") {
    val allVotes =
      Map(1 -> 50, 2 -> 20, 3 -> 15, 4 -> 10, 5 -> 5).map(asVotes)
    val districtSeats =
      Map(1 -> 150, 2 -> 80, 3 -> 60, 4 -> 40, 5 -> 20).map(asSeats)
    val allocator = new SeatAllocator(Seats(150))

    allocator.allocate(allVotes, districtSeats) shouldBe
      Map(1 -> 250, 2 -> 100, 3 -> 75, 4 -> 50, 5 -> 25).map(asSeats)
  }

  test("parliament example 2") {
    val allVotes =
      Map(1 -> 50, 2 -> 20, 3 -> 15, 4 -> 10, 5 -> 5).map(asVotes)
    val districtSeats =
      Map(1 -> 150, 2 -> 80, 3 -> 55, 4 -> 35, 5 -> 30).map(asSeats)
    val allocator = new SeatAllocator(Seats(150))

    allocator.allocate(allVotes, districtSeats) shouldBe
      Map(1 -> 247, 2 -> 99, 3 -> 74, 4 -> 50, 5 -> 30).map(asSeats)
  }
}
