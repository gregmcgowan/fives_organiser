package com.gregmcgowan.fivesorganiser.main.matchlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.getNonNull
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListNavigationEvents.AddMatchEvent
import com.gregmcgowan.fivesorganiser.main.matchlist.MatchListNavigationEvents.MatchSelected
import com.gregmcgowan.fivesorganiser.match.MatchOrchestrator
import timber.log.Timber
import javax.inject.Inject

class MatchListViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchOrchestrator: MatchOrchestrator,
        private val matchListUiModelReducers: MatchListUiModelReducers
) : CoroutinesViewModel(coroutineContext) {

    private val matchListUiModelLiveData = MutableLiveData<MatchListUiModel>()
    private val navigationLiveData = MutableLiveData<MatchListNavigationEvents>()

    init {
        matchListUiModelLiveData.value = MatchListUiModel(
                showList = false,
                showProgressBar = true,
                showEmptyView = false,
                emptyMessage = null,
                matches = emptyList()
        )

        navigationLiveData.value = MatchListNavigationEvents.Idle
    }

    fun uiModelLiveData(): LiveData<MatchListUiModel> {
        return matchListUiModelLiveData
    }

    fun navigationLiveData(): LiveData<MatchListNavigationEvents> {
        return navigationLiveData
    }

    fun onViewShown() {
        navigationLiveData.value = MatchListNavigationEvents.Idle
        updateUiModel(matchListUiModelReducers.loadingMatchListUiModel())
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = {
                    matchListUiModelReducers.matchListUiModel(matchOrchestrator.getAllMatches())
                },
                uiBlock = { uiModel -> updateUiModel(uiModel) }
        )
    }

    private fun updateUiModel(reducer: MatchListUiModelReducer) {
        matchListUiModelLiveData.value = reducer.invoke(matchListUiModelLiveData.getNonNull())
        Timber.d("Setting match list UI model to ${matchListUiModelLiveData.value}")
    }

    fun addMatchButtonPressed() {
        navigationLiveData.value = AddMatchEvent
    }

    fun matchSelected(matchId: String) {
        navigationLiveData.value = MatchSelected(matchId)
    }

}