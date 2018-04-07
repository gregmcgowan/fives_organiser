package com.gregmcgowan.fivesorganiser.match.summary

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.match.MatchStateHolder
import com.gregmcgowan.fivesorganiser.match.MatchTypeHelper
import timber.log.Timber
import javax.inject.Inject

class MatchSummaryViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchStateHolder: MatchStateHolder,
        private val matchSummaryUiModelReducers: MatchSummaryUiModelReducers,
        private val matchTypeHelper: MatchTypeHelper
) : CoroutinesViewModel(coroutineContext) {

    private val matchUiModelLiveData = MutableLiveData<MatchSummaryUiModel>()
    private val matchUiNavigationLiveData = MutableLiveData<MatchSummaryUiNavigationEvent>()

    init {
        val defaultModel = MatchSummaryUiModel(
                showContent = false,
                loading = true,
                success = false,
                errorMessage = null,
                date = "",
                startTime = "",
                endTime = "",
                location = "",
                selectedMatchTypeIndex = -1,
                matchTypeOptions = emptyList()
        )
        matchUiModelLiveData.value = defaultModel
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle

        runOnBackgroundAndUpdateOnUI({
            matchSummaryUiModelReducers.displayMatchReducer(matchStateHolder.match).invoke(defaultModel)
        }, { matchUiModelLiveData.value = it })
    }

    fun navigationEvents(): LiveData<MatchSummaryUiNavigationEvent> {
        return matchUiNavigationLiveData
    }

    fun matchUiModel(): LiveData<MatchSummaryUiModel> {
        return matchUiModelLiveData
    }

    fun onViewShown() {
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle
    }

    private fun updateUiModel(reducer: MatchUiModelReducer) {
        matchUiModelLiveData.value?.let {
            matchUiModelLiveData.value = reducer.invoke(it)
            Timber.d("Setting match UI model to ${matchUiModelLiveData.value}")
        }
    }

    fun startTimeSelected() {
        val match = matchStateHolder.match
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.ShowStartTimePicker(
                match.start.hour,
                match.start.minute
        )
    }

    fun endTimeSelected() {
        val match = matchStateHolder.match
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.ShowEndTimePicker(
                match.end.hour,
                match.end.minute
        )
    }

    fun dateSelected() {
        val match = matchStateHolder.match

        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.ShowDatePicker(
                match.start.year,
                match.start.month.value - 1,
                match.start.dayOfMonth
        )
    }

    fun dateUpdated(year: Int,
                    month: Int,
                    date: Int) {
        matchStateHolder.dateUpdated(year, month, date)
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle
        updateUiModel(matchSummaryUiModelReducers.dateUpdatedReducer(matchStateHolder.match))
    }

    fun startTimeUpdated(hour: Int,
                         minute: Int) {
        matchStateHolder.startTimeUpdated(hour, minute)
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle
        updateUiModel(matchSummaryUiModelReducers.startTimeUpdated(matchStateHolder.match))
    }

    fun endTimeUpdated(hour: Int, minute: Int) {
        matchStateHolder.endTimeUpdated(hour, minute)
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle
        updateUiModel(matchSummaryUiModelReducers.endTimeUpdated(matchStateHolder.match))
    }


    fun locationUpdated(location: String) {
        matchStateHolder.locationUpdated(location)
        updateUiModel(matchSummaryUiModelReducers.locationUpdatedReducer(matchStateHolder.match))
    }

    fun closeButtonPressed() {
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.CloseScreen
    }

    fun matchTypeSelected(matchType: String) {
        matchStateHolder.squadSizeUpdated(matchTypeHelper.getSquadSize(matchType))
        updateUiModel(matchSummaryUiModelReducers.matchTypeUpdated(matchStateHolder.match))
    }


}