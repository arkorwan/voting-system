package io.github.arkorwan.votingsystem.strategies
import io.github.arkorwan.votingsystem.models.{Party, Votes}

object NoStrategy extends Strategy {

  override val name = "No Strategy"

  override def reallocate(p: Party, allocation: Map[Party, Votes]) = allocation.filterKeys(_ == p)

}
