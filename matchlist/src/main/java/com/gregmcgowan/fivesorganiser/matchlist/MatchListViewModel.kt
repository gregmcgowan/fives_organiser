package com.gregmcgowan.fivesorganiser.matchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.match.Match
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import javax.inject.Inject

class MatchListViewModel @Inject constructor(
        private val getMatchUpdatesUseCase: GetMatchUpdatesUseCase,
        private val mapper: MatchListUiModelMappers,
        coroutineDisptachersAndContext: CoroutineDisptachersAndContext
) : CoroutinesViewModel(coroutineDisptachersAndContext) {

    val matchListUiModelLiveData: LiveData<MatchListUiModel>
        get() = _matchListUiModelLiveData

    private val _matchListUiModelLiveData = MediatorLiveData<MatchListUiModel>()

    val navigationLiveData: LiveData<MatchNavigationEvent>
        get() = _navigationLiveData

    private val _navigationLiveData = MutableLiveData<MatchNavigationEvent>()

    init {
        _matchListUiModelLiveData.value = MatchListUiModel(
                showList = false,
                showProgressBar = true,
                showEmptyView = false,
                emptyMessage = null,
                matches = emptyList()
        )

        _navigationLiveData.value = Idle

        _matchListUiModelLiveData.addSource(getMatchUpdatesUseCase.execute()) {
            it.either(
                    { exception -> handleException(exception) },
                    { matchUpdates -> handleMatchUpdates(matchUpdates) }
            )
        }

    }

    private fun handleMatchUpdates(matchUpdates: DataUpdate<Match>) {
        _matchListUiModelLiveData.postValue(mapper.map(_matchListUiModelLiveData.requireValue(), matchUpdates))
    }

    private fun handleException(exception: Exception): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addMatchButtonPressed() {
        _navigationLiveData.value = StartNewMatchFlow
    }

    fun editMatchDetails(matchId: String) {
        _navigationLiveData.value = ShowMatchDetails(matchId)
    }

    fun editSquad(matchId: String) {
        _navigationLiveData.value = ShowSquad(matchId)
    }

    override fun onCleared() {
        super.onCleared()
        getMatchUpdatesUseCase.cleanup()
    }
}