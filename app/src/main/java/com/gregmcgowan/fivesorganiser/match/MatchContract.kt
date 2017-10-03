package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LifecycleObserver
import io.reactivex.Observable

interface MatchContract {

    interface Ui {
        fun render(uiModel: MatchUiModel)

        fun dateUpdated(): Observable<MatchUiEvent>
        fun timeUpdated(): Observable<MatchUiEvent>
        fun locationUpdated(): Observable<MatchUiEvent>

        fun dateSelected(): Observable<MatchUiEvent>
        fun timeSelected(): Observable<MatchUiEvent>

        fun savePressed(): Observable<MatchUiEvent>

        fun backPressed(): Observable<MatchUiEvent>

    }

    interface Presenter : LifecycleObserver {
        fun startPresenting()
        fun stopPresenting()
    }

    sealed class MatchUiEvent {

        class UiShownEvent : MatchUiEvent()

        class TimeSelected : MatchUiEvent()
        class DateSelected : MatchUiEvent()

        class DateUpdatedEvent(val year: Int,
                               val month: Int,
                               val date: Int) : MatchUiEvent()

        class TimeUpdatedEvent(val hour: Int,
                               val minute: Int) : MatchUiEvent()

        class LocationUpdatedEvent(val location: String) : MatchUiEvent()

        class SaveButtonPressedEvent : MatchUiEvent()

        class BackArrowPressedEvent : MatchUiEvent()

    }

    sealed class MatchEventResults {
        class SaveStartedResult : MatchEventResults()
        class SaveFinishedResult : MatchEventResults()
    }

    data class MatchUiModel(val showContent: Boolean,
                            val success: Boolean,
                            val errorMessage: String?,
                            val date: String,
                            val time: String,
                            val location: String,
                            val closeScreen: Boolean = false,
                            val timePickerUiModel: TimePickerUiModel,
                            val datePickerUiModel: DatePickerUiModel, val loading: Boolean)

    sealed class TimePickerUiModel {
        object Idle : TimePickerUiModel()

        class ShowTimePickerUi(val hour: Int,
                               val minute: Int) : TimePickerUiModel()
    }

    sealed class DatePickerUiModel {
        object Idle : DatePickerUiModel()

        class ShowDatePickerUi(val year: Int,
                               val month: Int,
                               val date: Int) : DatePickerUiModel()
    }


}