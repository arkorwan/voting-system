package io.github.arkorwan.votingsystem.strategies

import io.github.arkorwan.votingsystem.models.{Party, Votes}

class ShiftingWinLoseStrategy(maxParties: Int) extends Strategy {

  override val name = "Shift excess votes to franchise (win/lose split)"

  override def reallocate(p: Party, allocation: Map[Party, Votes]) = {

    val franchiseParties = Stream.from(1).map(i => Party(p.id * 1000 + i, p.name + s" (${('A' + i).toChar})"))

    val votes = allocation(p)
    val maxOther = (allocation - p).values.map(_.value).max
    if (votes.value > maxOther) {
      val votesExcess = votes.value - maxOther - 1
      val partiesNeeded = 1 + (votesExcess - 1) / maxOther
      val (extraParties, baseline) = if (partiesNeeded >= maxParties) {
        val votesPerParty = 1 + (votes.value - 2) / maxParties
        (maxParties - 1, votesPerParty)
      } else {
        (partiesNeeded, maxOther)
      }
      val votesLastParty = votes.value - 1 - (baseline * extraParties)
      val pps = franchiseParties.take(extraParties).toList
      Map(
        p -> Votes(baseline + 1),
        pps.head -> Votes(votesLastParty)
      ) ++ pps.tail.map(pp => pp -> Votes(baseline))

    } else {
      Map(franchiseParties.head -> votes)
    }

  }

  override def reallocateAll(allocation: Map[Party, Votes]) = {
    val winningParty = allocation.maxBy(_._2.value)._1
    (allocation - winningParty) ++ reallocate(winningParty, allocation)
  }

}
