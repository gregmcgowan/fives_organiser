package com.gregmcgowan.fivesorganiser.matchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.match.*
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.*
import javax.inject.Inject

//TODO REVIEWTHIS
class GetMatchUpdatesUseCase @Inject constructor(private val matchRepo: MatchRepo,
                                                 private val squadRepo: MatchSquadRepo,
                                                 private val playersRepo: PlayerRepo,
                                                 private val matchMapper: MatchMapper,
                                                 private val coroutinesDisptachersAndContext: CoroutineDisptachersAndContext) {
    private val parent: Job = Job()

    private val uiScope = CoroutineScope(coroutinesDisptachersAndContext.ui + parent)
    private val mediatorLiveData = MediatorLiveData<Either<Exception, DataUpdate<Match>>>()

    init {
        mediatorLiveData.addSource(matchRepo.getMatchUpdates()) { update ->
            update?.either(
                    { exception -> mediatorLiveData.postValue(Either.Left(exception)) },
                    { matchUpdate -> mapUpdateToMatch(matchUpdate, ::mapMatch) }
            )
        }
        mediatorLiveData.addSource(squadRepo.getSquadUpdates()) { update ->
            update?.either(
                    { exception -> mediatorLiveData.postValue(Either.Left(exception)) },
                    { squadUpdate ->  mapUpdateToMatch(squadUpdate, ::mapSquad)}
            )
        }
    }

    fun execute(): LiveData<Either<Exception, DataUpdate<Match>>> {
        return mediatorLiveData
    }

    fun cleanup() {
        parent.cancel()
    }

    private fun <T> mapUpdateToMatch(input: DataUpdate<T>,
                                     map: suspend (DataUpdate<T>) -> DataUpdate<Match>) {
        uiScope.launch(coroutinesDisptachersAndContext.context) {
            val dataUpdate = async(coroutinesDisptachersAndContext.io) { map.invoke(input) }.await()
            mediatorLiveData.postValue(Either.Right(dataUpdate))
        }
    }

    // TODO maybe create a class that creates a match from match enitity or squad entity
    // internally it could look up the players rather than loading them first in this classs

    private suspend fun mapMatch(matchUpdate: DataUpdate<MatchEntity>): DataUpdate<Match> {
        return playersRepo.getPlayers().associateBy { it.playerId }.run {
            DataUpdate(matchUpdate.changes.map {
                DataChange(it.type, matchMapper.map(it.data, squadRepo.getMatchSquad(it.data.matchId), this))
            })
        }
    }

    private suspend fun mapSquad(squadUpdate: DataUpdate<MatchSquadEntity>): DataUpdate<Match> {
        return playersRepo.getPlayers().associateBy { it.playerId }.run {
            DataUpdate(squadUpdate.changes.map {
                DataChange(it.type, matchMapper.map(matchRepo.getMatch(it.data.matchId), it.data, this))
            })
        }
    }

}


