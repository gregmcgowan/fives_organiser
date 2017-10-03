package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.data.match.Match

class Reducers(private val instantFormatter: ZonedDateTimeFormatter) {

    internal fun savingReducer(): CreateMatchModelReducer = { model ->
        model.copy(loading = true, showContent = false)
    }

    internal fun dateSelectedReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                timePickerUiModel = MatchContract.TimePickerUiModel.Idle,
                datePickerUiModel = MatchContract.DatePickerUiModel.ShowDatePickerUi(
                        match.dateTime.year,
                        match.dateTime.month.value,
                        match.dateTime.dayOfMonth
                )
        )
    }

    internal fun dateUpdatedReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                timePickerUiModel = MatchContract.TimePickerUiModel.Idle,
                datePickerUiModel = MatchContract.DatePickerUiModel.Idle,
                date = instantFormatter.formatDate(match.dateTime))
    }

    internal fun timeSelectedReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                timePickerUiModel = MatchContract.TimePickerUiModel.ShowTimePickerUi(match.dateTime.hour, match.dateTime.minute),
                datePickerUiModel = MatchContract.DatePickerUiModel.Idle
        )
    }

    internal fun timeUpdatedReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                time = instantFormatter.formatTime(match.dateTime),
                timePickerUiModel = MatchContract.TimePickerUiModel.Idle
        )
    }

    internal fun locationUpdatedReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                location = match.location,
                timePickerUiModel = MatchContract.TimePickerUiModel.Idle,
                datePickerUiModel = MatchContract.DatePickerUiModel.Idle
        )
    }

    internal fun closeScreenReducer(): CreateMatchModelReducer = { model ->
        model.copy(closeScreen = true)
    }

    internal fun displayMatchReducer(match: Match): CreateMatchModelReducer = { model ->
        model.copy(
                loading = false,
                showContent = true,
                success = true,
                location = match.location,
                date = instantFormatter.formatDate(match.dateTime),
                time = instantFormatter.formatTime(match.dateTime)
        )
    }
}
