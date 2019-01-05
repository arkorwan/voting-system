package io.github.arkorwan.votingsystem.strategies

import io.github.arkorwan.votingsystem.models.{Party, Votes}

trait Strategy {

  val name: String

  protected def reallocate(p: Party, allocation: Map[Party, Votes]): Map[Party, Votes]

  final def apply(p: Party, allocation: Map[Party, Votes]): Map[Party, Votes] = {

    val reallocation = reallocate(p, allocation)
    val otherParties = allocation - p

    assert(reallocation.keySet.intersect(otherParties.keySet).isEmpty, "reassignment of other parties no allowed")
    assert(reallocation.values.reduce(_ + _) == allocation(p), "votes before and after reallocation must be equal")

    otherParties ++ reallocation
  }

}
