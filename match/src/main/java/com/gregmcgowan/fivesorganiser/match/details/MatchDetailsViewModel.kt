package com.gregmcgowan.fivesorganiser.match.details

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.Dispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import com.gregmcgowan.fivesorganiser.core.setIfDifferent
import com.gregmcgowan.fivesorganiser.data.match.Match
import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import com.gregmcgowan.fivesorganiser.data.match.MatchTypeHelper
import com.gregmcgowan.fivesorganiser.data.match.Squad
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsNavEvent.*
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

private const val DEFAULT_NO_OF_PLAYERS = 10L
internal const val NEW_MATCH_ID = ""

class MatchDetailsViewModel @Inject constructor(
        private val matchId: String?,
        private val matchTypeHelper: MatchTypeHelper,
        private val mapper: MatchDetailsUiModelMapper,
        private val matchInteractor: MatchInteractor,
        dispatchers: Dispatchers
) : CoroutinesViewModel(dispatchers) {

    private lateinit var match: Match

    private val _uiModelLiveData = MutableLiveData<MatchDetailsUiModel>()

    val uiModelLiveData: LiveData<MatchDetailsUiModel>
        get() = _uiModelLiveData

    private val _navLiveData = MutableLiveData<MatchDetailsNavEvent>()

    val navLiveData: LiveData<MatchDetailsNavEvent>
        get() = _navLiveData

    init {
        _navLiveData.value = Idle
        _uiModelLiveData.value = MatchDetailsUiModel(
                loading = true,
                showContent = false,
                showErrorState = false,
                date = "",
                startTime = "",
                endTime = "",
                location = "",
                showCreateSquadButton = false,
                matchTypeOptions = emptyList(),
                selectedMatchTypeIndex = -1
        )

        launch({
            match = matchId?.let { matchInteractor.getMatch(it) } ?: createDefault()
            mapper.map(match)
        }, { _uiModelLiveData.value = it })

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
        launch({
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

            launch(
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
        launch(
                backgroundBlock = { mapper.map(match) },
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

    fun matchTypeSelected(matchType: String) {
        val expectedSize = matchTypeHelper.getSquadSize(matchType).toLong()
        match = match.copy(squad = match.squad.copy(expectedSize = expectedSize))
    }


}