package io.github.arkorwan.votingsystem.strategies

import io.github.arkorwan.votingsystem.models.{Party, Votes}

class SplitProfitLossStrategy(cost: Double) extends Strategy {

  override val name = "Split by profit/loss district"

  override def reallocate(p: Party, allocation: Map[Party, Votes]) = {

    val franchiseParty = Party(1000, p.name + " (B)")

    val votes = allocation(p)
    if(votes.value < cost && allocation.values.forall(_.value <= votes.value)){
      Map(p -> votes)
    } else {
      Map(franchiseParty -> votes)
    }

  }

}
