package io.github.arkorwan.votingsystem.services

import io.github.arkorwan.votingsystem.models._

import scala.annotation.tailrec

class SeatAllocator(val listSeats: Seats) {

  def allocate(districtResults: Seq[DistrictResult]): Map[Party, Seats] = {

    // votes by party
    val allVotes: Map[Party, Votes] = districtResults
      .flatMap(_.votesByParty)
      .groupBy(_._1)
      .mapValues(_.map(_._2).reduce(_ + _))

    val districtSeats = countDistrictSeats(districtResults)

    allocate(allVotes, districtSeats)
  }

  def allocate(allVotes: Map[Party, Votes], districtSeats: Map[Party, Seats]): Map[Party, Seats] = {
    val districtSeatsWithDefault = districtSeats.withDefaultValue(Seats(0))
    val quota = computeAdjustedQuota(allVotes, districtSeatsWithDefault)
    val rawListSeats = quota.map { case (p, q) =>
      val remaining = q.value - districtSeatsWithDefault(p).value
      val fullPart = remaining.floor.toInt
      val fractionPart = remaining - fullPart
      (p, (Seats(fullPart), fractionPart))
    }

    val fractionalGainers = selectFractionalGainers(rawListSeats)

    allVotes.keys.map { p =>
      val seats = districtSeatsWithDefault(p) + rawListSeats(p)._1 +
        (if (fractionalGainers.contains(p)) Seats(1) else Seats(0))
      (p, seats)
    }.toMap
  }


  protected def countDistrictSeats(districtResults: Seq[DistrictResult]): Map[Party, Seats] = {
    districtResults.map { case DistrictResult(_, votes) => votes.maxBy(v => (v._2.value, /*tiebreaker*/ -v._1.id))._1 }
      // winning party in each district
      .groupBy(identity)
      .mapValues(n => Seats(n.size))
  }

  protected def computeAdjustedQuota(allVotes: Map[Party, Votes], districtSeats: Map[Party, Seats]): Map[Party, Quota] = {

    // in each iteration, calculate the quota for each party and compare to the number of district seats won
    // if some parties have more seats than the quota, we need to take them (along with their seats) out of consideration
    // and reallocate. Repeat until we can successfully allocate the quota.
    @tailrec
    def iterate(remainVotes: Map[Party, Votes], remainSeats: Seats, allocated: Map[Party, Quota]): Map[Party, Quota] = {
      val totalVotes = remainVotes.values.reduce(_ + _)
      val multiplier = remainSeats.value.toDouble / totalVotes.value
      val quota = remainVotes.mapValues(v => Quota(v.value * multiplier))
      val (overQuota, underQuota) = quota.partition { case (k, v) => districtSeats(k).value > v.value }
      if (overQuota.isEmpty) {
        allocated ++ underQuota
      } else {
        val toAllocate = districtSeats.filterKeys(overQuota.keySet.contains)
        iterate(
          remainVotes.filterKeys(underQuota.keySet.contains),
          remainSeats - toAllocate.values.reduce(_ + _),
          allocated ++ toAllocate.mapValues(v => Quota(v.value))
        )
      }

    }

    iterate(allVotes, districtSeats.values.reduce(_ + _) + listSeats, Map.empty)
  }

  protected def selectFractionalGainers(rawListSeats: Map[Party, (Seats, Double)]): Set[Party] = {
    val remainingSeats = listSeats - rawListSeats.values.map(_._1).reduce(_ + _)
    rawListSeats.mapValues(_._2).toSeq // take only fractional part
      .sortBy(v => (-v._2, /*tiebreaker*/ v._1.id)) // sort by fractional value (DESC) then by party id (ASC)
      .take(remainingSeats.value) // select n parties to receive 1 seat each
      .map(_._1).toSet
  }

}
