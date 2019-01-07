package io.github.arkorwan.votingsystem.strategies
import io.github.arkorwan.votingsystem.models.{Party, Votes}

object NoStrategy extends Strategy {

  override val name = "No Strategy"

  override protected def reallocate(p: Party, allocation: Map[Party, Votes]) = allocation.filterKeys(_ == p)

  override protected def reallocateAll(allocation: Map[Party, Votes]) = allocation

}
