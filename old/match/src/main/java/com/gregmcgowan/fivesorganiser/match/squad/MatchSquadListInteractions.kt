package com.gregmcgowan.fivesorganiser.match.squad

interface MatchSquadListInteractions {

    fun playerUpdated(playerId : String, status : MatchSquadListPlayerStatus)

}