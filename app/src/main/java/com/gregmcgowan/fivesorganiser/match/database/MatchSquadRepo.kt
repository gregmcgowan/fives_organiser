package com.gregmcgowan.fivesorganiser.match.database

interface MatchSquadRepo {

    suspend fun createMatchSquad(matchId: String,
                                 invited: List<String> = emptyList(),
                                 confirmed: List<String> = emptyList(),
                                 unsure: List<String> = emptyList(),
                                 declined: List<String> = emptyList()): MatchSquadEntity


    suspend fun saveMatchSquad(matchSquad: MatchSquadEntity)


    suspend fun getMatchSquad(matchId: String): MatchSquadEntity

}