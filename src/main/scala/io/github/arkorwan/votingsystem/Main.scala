package io.github.arkorwan.votingsystem

import io.github.arkorwan.votingsystem.datasources.CSVDataSource
import io.github.arkorwan.votingsystem.models.{DistrictResult, Seats}
import io.github.arkorwan.votingsystem.services.SeatAllocator
import io.github.arkorwan.votingsystem.strategies._
import monocle.macros.GenLens

object Main extends App {

  val districtResults = new CSVDataSource().read()
  val allocator = new SeatAllocator(Seats(161))
  val allSeats = Seats(districtResults.length) + allocator.listSeats
  val allVotes = districtResults.flatMap(_.votesByParty.values).reduce(_ + _)
  val votesPerSeat = allVotes.value.toDouble / allSeats.value

  val allParties = districtResults.flatMap(_.votesByParty.keySet).distinct
  val focusParty = allParties.find(_.name == "เพื่อไทย").get
  val otherParties = allParties.filterNot(_ == focusParty)

  val votesLens = GenLens[DistrictResult](_.votesByParty)
  val applyStrategy = (str: Strategy) => districtResults.map(votesLens.modify(a => str.apply(focusParty, a)))

  val strategies = Seq(
    NoStrategy,
    SplitWinLoseStrategy,
    new SplitProfitLossStrategy(votesPerSeat),
    new ShiftingWinLoseStrategy(5)
  )

  strategies foreach { str =>
    val appliedResults = applyStrategy(str)
    val allocation = allocator.allocate(appliedResults)
    val alliedSeats = allocation.filterKeys(p => !otherParties.contains(p))

    println(s"Strategy: ${str.name}")
    alliedSeats.toSeq.sortBy(_._1.id) foreach { s =>
      println(s"  Party: ${s._1.name} => ${s._2.value}")
    }
    println(s"Total: ${alliedSeats.values.reduce(_ + _).value}")
    println

  }

}
