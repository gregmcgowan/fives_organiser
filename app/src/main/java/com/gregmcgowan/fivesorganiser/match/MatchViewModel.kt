package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LiveData
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.match.Match
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.core.ui.NonNullMutableLiveData
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

typealias MatchUiModelReducer = (MatchUiModel) -> MatchUiModel

class MatchViewModel(ui: CoroutineContext,
                     background: CoroutineContext,
                     private val matchId: String?,
                     private val matchRepo: MatchRepo,
                     private val matchUiModelReducers: MatchUiModelReducers) : CoroutinesViewModel(ui, background) {

    private val matchUiModelLiveData = NonNullMutableLiveData(
            MatchUiModel(
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
    )
    private val matchUiNavigationLiveData = NonNullMutableLiveData<MatchUiNavigationEvent>(
            MatchUiNavigationEvent.Idle
    )

    private var match: Match

    init {
        match = Match("",
                "",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                10)
        if (matchId != null) {
            runOnBackgroundAndUpdateOnUI(
                    { match = matchRepo.getMatch(matchId) },
                    { updateUiModel(matchUiModelReducers.displayMatchReducer(match, false)) }
            )
        } else {
            updateUiModel(matchUiModelReducers.displayMatchReducer(match, true))
        }
    }

    fun navigationEvents(): LiveData<MatchUiNavigationEvent> {
        return matchUiNavigationLiveData
    }

    fun matchUiModel(): LiveData<MatchUiModel> {
        return matchUiModelLiveData
    }

    fun onViewShown() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
    }

    private fun updateUiModel(reducer: MatchUiModelReducer) {
        matchUiModelLiveData.value?.let {
            matchUiModelLiveData.value = reducer.invoke(it)
            Timber.d("Setting match UI model to ${matchUiModelLiveData.value}")
        }
    }

    fun startTimeSelected() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.ShowStartTimePicker(
                match.start.hour,
                match.start.minute
        )
    }

    fun endTimeSelected() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.ShowEndTimePicker(
                match.end.hour,
                match.end.minute
        )
    }

    fun dateSelected() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.ShowDatePicker(
                match.start.year,
                match.start.month.value - 1,
                match.start.dayOfMonth
        )
    }

    fun dateUpdated(year: Int,
                    month: Int,
                    date: Int) {
        val dateTime = ZonedDateTime.of(
                year,
                month + 1,
                date,
                match.start.hour,
                match.start.minute,
                match.start.second,
                match.start.nano,
                match.start.zone
        )
        match = match.copy(start = dateTime)
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
        updateUiModel(matchUiModelReducers.dateUpdatedReducer(match))
    }

    fun startTimeUpdated(hour: Int,
                         minute: Int) {
        match = match.copy(
                start = ZonedDateTime.of(
                        match.start.year,
                        match.start.month.value,
                        match.start.dayOfMonth,
                        hour,
                        minute,
                        match.start.second,
                        match.start.nano,
                        match.start.zone
                ))
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
        updateUiModel(matchUiModelReducers.startTimeUpdated(match))
    }

    fun endTimeUpdated(hour: Int, minute: Int) {
        match = match.copy(
                end = ZonedDateTime.of(
                        match.end.year,
                        match.end.month.value,
                        match.end.dayOfMonth,
                        hour,
                        minute,
                        match.end.second,
                        match.end.nano,
                        match.end.zone
                ))
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
        updateUiModel(matchUiModelReducers.endTimeUpdated(match))
    }


    fun locationUpdated(location: String) {
        match = match.copy(location = location)
        updateUiModel(matchUiModelReducers.locationUpdatedReducer(match))
    }

    fun squadSizeUpdated(numberOfPlayers: Int) {
        match = match.copy(squadSize = numberOfPlayers)
        updateUiModel(matchUiModelReducers.numberOfPlayersUpdatedReduce(match))
    }

    fun saveButtonPressed() {
        updateUiModel(matchUiModelReducers.savingUiModel())
        saveOrCreate()
    }

    private fun saveOrCreate() {
        runOnBackgroundAndUpdateOnUI(
                {
                    if (matchId == null) {
                        matchRepo.createMatch(match.start, match.end, match.squadSize, match.location)
                    } else {
                        matchRepo.saveMatch(match)
                    }
                },
                { matchUiNavigationLiveData.value = MatchUiNavigationEvent.CloseScreen }
        )
    }

    fun closeButtonPressed() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.CloseScreen
    }


}