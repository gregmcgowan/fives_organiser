package com.gregmcgowan.fivesorganiser.match.timelocation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.core.setIfDifferent
import com.gregmcgowan.fivesorganiser.match.Match
import com.gregmcgowan.fivesorganiser.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.match.Squad
import com.gregmcgowan.fivesorganiser.match.timelocation.MatchTimeAndLocationNavEvent.*
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

const val DEFAULT_NO_OF_PLAYERS = 10L
internal const val NEW_MATCH_ID = ""

class MatchTimeAndLocationViewModel @Inject constructor(
        coroutineContexts: CoroutineContexts,
        private val matchId: String?,
        private val matchTimeAndLocationUiModelMapper: MatchTimeAndLocationUiModelMapper,
        private val matchInteractor: MatchInteractor
) : CoroutinesViewModel(coroutineContexts) {

    private lateinit var match: Match

    private val _uiModelLiveData = MutableLiveData<MatchTimeAndLocationUiModel>()

    val uiModelLiveData: LiveData<MatchTimeAndLocationUiModel>
        get() = _uiModelLiveData

    private val _navLiveData = MutableLiveData<MatchTimeAndLocationNavEvent>()

    val navLiveData: LiveData<MatchTimeAndLocationNavEvent>
        get() = _navLiveData

    init {
        _navLiveData.value = Idle
        _uiModelLiveData.value = MatchTimeAndLocationUiModel(
                loading = true,
                showContent = false,
                showErrorState = false,
                date = "",
                startTime = "",
                endTime = "",
                location = "",
                showCreateSquadButton = false
        )

        runOnBackgroundAndUpdateOnUI({
            match = matchId?.let { matchInteractor.getMatch(it) } ?: createDefault()
            matchTimeAndLocationUiModelMapper.map(match)
        }, {
            _uiModelLiveData.value = it
        }
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

        _navLiveData.value = Idle
        mapAndUpdateUi()
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
                )
        )
        _navLiveData.value = Idle
        mapAndUpdateUi()
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
                )
        )
        _navLiveData.value = Idle
        mapAndUpdateUi()
    }

    fun locationUpdated(location: String) {
        match = match.copy(location = location)
        _navLiveData.value = Idle
        mapAndUpdateUi()
    }

    fun startTimeSelected() {
        _navLiveData.value = ShowStartTimePicker(
                match.start.hour,
                match.start.minute
        )
    }

    fun endTimeSelected() {
        _navLiveData.value = ShowEndTimePicker(
                match.end.hour,
                match.end.minute
        )
    }

    fun dateSelected() {
        _navLiveData.value = ShowDatePicker(
                match.start.year,
                match.start.month.value - 1,
                match.start.dayOfMonth
        )
    }

    fun nextButtonPressed() {
        //TODO validation
        _uiModelLiveData.value = _uiModelLiveData.requireValue().copy(
                loading = true,
                showContent = false,
                showCreateSquadButton = false
        )
        runOnBackgroundAndUpdateOnUI({
            createOrSave()
        }, {
            //TODO handle errors
            val event = GoToSquadScreenButtonPressed(match.matchId)
            _navLiveData.value = event
        })

    }

    fun backPressed() {
        if (matchId != null) {
            _uiModelLiveData.value = _uiModelLiveData.requireValue().copy(
                    loading = true,
                    showContent = false,
                    showCreateSquadButton = false
            )

            runOnBackgroundAndUpdateOnUI(
                    { createOrSave() },
                    //TODO handle errors
                    { _navLiveData.value = BackPressed })
        } else {
            // TODO check if any thing has been entered and prompt to save
            _navLiveData.value = BackPressed
        }
    }

    private fun createDefault(): Match =
            Match(matchId = NEW_MATCH_ID,
                    start = ZonedDateTime.now(),
                    end = ZonedDateTime.now().plusHours(1),
                    location = "",
                    squad = Squad(DEFAULT_NO_OF_PLAYERS)
            )


    private fun mapAndUpdateUi() {
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { matchTimeAndLocationUiModelMapper.map(match) },
                uiBlock = { _uiModelLiveData.setIfDifferent(it) }
        )
    }

    private suspend fun createOrSave() {
        if (matchId == null) {
            match = matchInteractor.createMatch(
                    match.start,
                    match.end,
                    DEFAULT_NO_OF_PLAYERS,
                    match.location
            )
        } else {
            matchInteractor.saveMatch(match)
        }
    }


}