package com.gregmcgowan.fivesorganiser.match.database

interface MatchSquadRepo {

    suspend fun createMatchSquad(matchId: String,
                                 invited: List<String>,
                                 confirmed: List<String>,
                                 unsure: List<String>,
                                 declined: List<String>): MatchSquadEntity


    suspend fun saveMatchSquad(matchSquad: MatchSquadEntity)


    suspend fun getMatchSquad(matchId: String): MatchSquadEntity

}