package io.github.arkorwan.votingsystem.models

case class DistrictResult(district: District, votesByParty: Map[Party, Votes]) {

  override def toString: String = {
    val parties = votesByParty.toSeq.sortBy(_._1.id).map { case (p, v) => s"$p: ${v.value}" }.mkString("|")
    s"District #$district: [$parties]"
  }

}
