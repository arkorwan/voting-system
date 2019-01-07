package io.github.arkorwan.votingsystem

import io.github.arkorwan.votingsystem.datasources.CSVDataSource
import io.github.arkorwan.votingsystem.models.{DistrictResult, Seats}
import io.github.arkorwan.votingsystem.services.SeatAllocator
import io.github.arkorwan.votingsystem.strategies._
import monocle.macros.GenLens

object LevelFieldMain extends App {

  val districtResults = new CSVDataSource().read()
  val allocator = new SeatAllocator(Seats(161))
  val allSeats = Seats(districtResults.length) + allocator.listSeats
  val allVotes = districtResults.flatMap(_.votesByParty.values).reduce(_ + _)
  val votesPerSeat = allVotes.value.toDouble / allSeats.value

  val allParties = districtResults.flatMap(_.votesByParty.keySet).distinct

  val votesLens = GenLens[DistrictResult](_.votesByParty)
  val applyAllStrategy = (str: Strategy) => districtResults.map(votesLens.modify(a => str.applyAll(a)))

  val strategies = Seq(
    NoStrategy,
    SplitRandomStrategy,
    SplitWinLoseStrategy,
    new SplitProfitLossStrategy(votesPerSeat),
    new ShiftingWinLoseStrategy(5)
  )

  strategies foreach { str =>
    val appliedResults = applyAllStrategy(str)
    val allocation = allocator.allocate(appliedResults)

    println(s"Strategy: ${str.name}")
    allocation.groupBy(a => if (a._1.id < 1000) a._1.id else a._1.id / 1000)
      .map { case (_, s) => s.minBy(_._1.id)._1 -> s.values.reduce(_ + _) }
      .toSeq
      .filter(_._2.value > 0)
      .sortBy(_._2.value * -1)
      .foreach { case (p, s) => println(s"${p.name}: ${s.value}") }
    println
  }

}
