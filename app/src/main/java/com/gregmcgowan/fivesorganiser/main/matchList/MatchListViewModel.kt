package com.gregmcgowan.fivesorganiser.main.matchList

import android.arch.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.core.ui.NonNullMutableLiveData
import com.gregmcgowan.fivesorganiser.main.matchList.MatchListNavigationEvents.AddMatchEvent
import com.gregmcgowan.fivesorganiser.main.matchList.MatchListNavigationEvents.MatchSelected
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

class MatchListViewModel(ui: CoroutineContext,
                         background: CoroutineContext,
                         private val matchRepo: MatchRepo,
                         private val dateTimeFormatter: ZonedDateTimeFormatter) : CoroutinesViewModel(ui, background) {

    private val matchListUiModelLiveData = NonNullMutableLiveData(
            MatchListUiModel(
                    showList = false,
                    showProgressBar = true,
                    showEmptyView = false,
                    emptyMessage = null,
                    matches = emptyList()
            )
    )
    private val navigationLiveData = NonNullMutableLiveData<MatchListNavigationEvents>(
            MatchListNavigationEvents.Idle
    )

    fun uiModelLiveData(): LiveData<MatchListUiModel> {
        return matchListUiModelLiveData
    }

    fun navigationLiveData(): LiveData<MatchListNavigationEvents> {
        return navigationLiveData
    }

    fun onViewShown() {
        navigationLiveData.value = MatchListNavigationEvents.Idle
        updateUiModel(loadingMatchListUiModel())
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { matchListUiModel(matchRepo.getAllMatches(), dateTimeFormatter) },
                uiBlock = { uiModel -> updateUiModel(uiModel) }
        )
    }

    private fun updateUiModel(reducer: MatchListUiModelReducer) {
        Timber.d("Setting match list UI model to ${matchListUiModelLiveData.value}")
        matchListUiModelLiveData.value = reducer.invoke(matchListUiModelLiveData.getNonNullValue())
    }

    fun addMatchButtonPressed() {
        navigationLiveData.value = AddMatchEvent
    }

    fun matchSelected(matchId: String) {
        navigationLiveData.value = MatchSelected(matchId)
    }

}