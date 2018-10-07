package com.gregmcgowan.fivesorganiser.matchlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import timber.log.Timber
import javax.inject.Inject

class MatchListViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchInteractor: MatchInteractor,
        private val matchListUiModelMappers: MatchListUiModelMappers
) : CoroutinesViewModel(coroutineContext) {

    val matchListUiModelLiveData: LiveData<MatchListUiModel>
        get() = _matchListUiModelLiveData
    private val _matchListUiModelLiveData = MutableLiveData<MatchListUiModel>()

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
    }


    fun onViewShown() {
        _navigationLiveData.value = Idle

        updateUiModel(
                _matchListUiModelLiveData.requireValue().copy(
                        showList = false,
                        showProgressBar = true,
                        showEmptyView = false
                )
        )

        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { matchListUiModelMappers.map(matchInteractor.getAllMatches()) },
                uiBlock = { uiModel -> updateUiModel(uiModel) }
        )
    }

    private fun updateUiModel(model: MatchListUiModel) {
        Timber.d("Setting match list UI model to ${_matchListUiModelLiveData.value}")
        _matchListUiModelLiveData.value = model

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

}