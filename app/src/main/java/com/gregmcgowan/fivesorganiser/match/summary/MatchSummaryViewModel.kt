package com.gregmcgowan.fivesorganiser.match.summary

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.getNonNull
import com.gregmcgowan.fivesorganiser.match.MatchOrchestrator
import com.gregmcgowan.fivesorganiser.match.MatchStateHolder
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

class MatchSummaryViewModel @Inject constructor(coroutineContext: CoroutineContexts,
                                                private val matchId: String?,
                                                private val matchOrchestrator: MatchOrchestrator,
                                                private val matchSummaryUiModelReducers: MatchSummaryUiModelReducers) : CoroutinesViewModel(coroutineContext) {

    private val matchUiModelLiveData = MutableLiveData<MatchSummaryUiModel>()
    private val matchUiNavigationLiveData = MutableLiveData<MatchSummaryUiNavigationEvent>()

    private lateinit var matchStateHolder: MatchStateHolder

    init {
        matchUiModelLiveData.value = MatchSummaryUiModel(
                title = "",
                showContent = false,
                loading = true,
                success = false,
                errorMessage = null,
                date = "",
                startTime = "",
                endTime = "",
                location = "",
                numberOfPLayers = ""
        )

        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.Idle

        runOnBackgroundAndUpdateOnUI({
            if (matchId == null) {
                matchStateHolder = MatchStateHolder(matchOrchestrator.createMatch(
                        startTime = ZonedDateTime.now(),
                        endTime = ZonedDateTime.now().plusHours(1),
                        squadSize = 10,
                        location = "")
                )
            } else {
                matchStateHolder = MatchStateHolder(matchOrchestrator.getMatch(matchId))
            }
        },
                { updateUiModel(matchSummaryUiModelReducers.displayMatchReducer(matchStateHolder.match, matchId == null)) }
        )
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

            Timber.d("Setting match UI model to ${matchUiModelLiveData.value}")
            matchUiModelLiveData.value = reducer.invoke(matchUiModelLiveData.getNonNull())
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

    fun squadSizeUpdated(numberOfPlayers: Int) {
        matchStateHolder.squadSizeUpdated(numberOfPlayers)
        updateUiModel(matchSummaryUiModelReducers.numberOfPlayersUpdatedReduce(matchStateHolder.match))
    }

    fun saveButtonPressed() {
        updateUiModel(matchSummaryUiModelReducers.savingUiModel())
        runOnBackgroundAndUpdateOnUI(
                { matchOrchestrator.saveMatch(matchStateHolder.match) },
                { matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.CloseScreen }
        )
    }

    fun closeButtonPressed() {
        matchUiNavigationLiveData.value = MatchSummaryUiNavigationEvent.CloseScreen
    }


}