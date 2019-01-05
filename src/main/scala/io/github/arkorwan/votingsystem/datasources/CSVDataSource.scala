package io.github.arkorwan.votingsystem.datasources

import io.github.arkorwan.votingsystem.models._
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class CandidateResult(province: String, district: Int, candidate: String, num: Int,
                           party: String, formattedVotes: String)

class CSVDataSource {

  def read(): Seq[DistrictResult] = {
    val inputStream = getClass.getResourceAsStream("/district_results_54.csv")
    val reads = inputStream.asCsvReader[CandidateResult](rfc.withHeader)

    val allCandidates: Seq[CandidateResult] = reads.toSeq.flatMap(_.right.toOption)

    val parties = allCandidates.map(_.party).distinct.zipWithIndex.toMap.map{ case (name, id) =>
      name -> Party(id, name)
    }

    allCandidates.groupBy(c => (c.province, c.district)).toSeq.zipWithIndex.map { case ((_, results), index) =>
      val resultMap = results.map(c => parties(c.party) -> Votes(c.formattedVotes.filter(_.isDigit).toInt)).toMap
      DistrictResult(District(index), resultMap)
    }

  }


}
