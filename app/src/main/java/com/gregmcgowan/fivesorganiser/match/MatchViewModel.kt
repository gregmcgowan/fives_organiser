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
                    showContent = false,
                    loading = true,
                    success = false,
                    errorMessage = null,
                    date = "",
                    time = "",
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
                0)
        if (matchId != null) {
            runOnBackgroundAndUpdateOnUI(
                    { match = matchRepo.getMatch(matchId) },
                    { updateUiModel(matchUiModelReducers.displayMatchReducer(match)) }
            )
        } else {
            updateUiModel(matchUiModelReducers.displayMatchReducer(match))
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

    fun timeSelected() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.ShowTimePicker(
                match.dateTime.hour,
                match.dateTime.minute
        )
    }

    fun dateSelected() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.ShowDatePicker(
                match.dateTime.year,
                match.dateTime.month.value - 1,
                match.dateTime.dayOfMonth
        )
    }

    fun dateUpdated(year: Int,
                    month: Int,
                    date: Int) {
        val dateTime = ZonedDateTime.of(
                year,
                month + 1,
                date,
                match.dateTime.hour,
                match.dateTime.minute,
                match.dateTime.second,
                match.dateTime.nano,
                match.dateTime.zone
        )
        match = match.copy(dateTime = dateTime)
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
        updateUiModel(matchUiModelReducers.dateUpdatedReducer(match))
    }

    fun timeUpdated(hour: Int,
                    minute: Int) {
        match = match.copy(
                dateTime = ZonedDateTime.of(
                        match.dateTime.year,
                        match.dateTime.month.value,
                        match.dateTime.dayOfMonth,
                        hour,
                        minute,
                        match.dateTime.second,
                        match.dateTime.nano,
                        match.dateTime.zone
                ))
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.Idle
        updateUiModel(matchUiModelReducers.timeUpdatedReducer(match))
    }

    fun locationUpdated(location: String) {
        match = match.copy(location = location)
        updateUiModel(matchUiModelReducers.locationUpdatedReducer(match))
    }

    fun numberOfPlayersUpdated(numberOfPlayers: Int) {
        match = match.copy(numberOfPlayers = numberOfPlayers)
        updateUiModel(matchUiModelReducers.numberOfPlayersUpdatedReduce(match))
    }

    fun saveButtonPressed() {
        updateUiModel(matchUiModelReducers.savingUiModel())
        saveOrCreate()
    }

    private fun saveOrCreate() {
        runOnBackgroundAndUpdateOnUI({
            if (matchId == null) {
                matchRepo.createMatch(match.dateTime, match.location, match.numberOfPlayers)
            } else {
                matchRepo.saveMatch(match)
            }
        },
                { matchUiNavigationLiveData.value = MatchUiNavigationEvent.CloseScreen })
    }

    fun closeButtonPressed() {
        matchUiNavigationLiveData.value = MatchUiNavigationEvent.CloseScreen
    }

}