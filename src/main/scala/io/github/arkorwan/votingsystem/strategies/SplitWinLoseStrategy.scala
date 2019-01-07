package io.github.arkorwan.votingsystem.strategies

import io.github.arkorwan.votingsystem.models.{Party, Votes}

object SplitWinLoseStrategy extends Strategy {

  override val name = "Split by winning/losing district"

  override def reallocate(p: Party, allocation: Map[Party, Votes]) = {

    val franchiseParty = Party(p.id * 1000, p.name + " (B)")

    val votes = allocation(p)
    if(allocation.values.exists(_.value > votes.value)){
      Map(franchiseParty -> votes)
    } else {
      Map(p -> votes)
    }

  }

}
