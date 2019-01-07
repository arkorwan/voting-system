package io.github.arkorwan.votingsystem.strategies

import io.github.arkorwan.votingsystem.models.{Party, Votes}

import scala.util.Random

object SplitRandomStrategy extends Strategy {

  override val name = "Split randomly"

  override protected def reallocate(p: Party, allocation: Map[Party, Votes]) = {

    val franchiseParty = Party(p.id *1000, p.name + " (B)")

    val votes = allocation(p)
    if(Random.nextBoolean()){
      Map(franchiseParty -> votes)
    } else {
      Map(p -> votes)
    }

  }

}
