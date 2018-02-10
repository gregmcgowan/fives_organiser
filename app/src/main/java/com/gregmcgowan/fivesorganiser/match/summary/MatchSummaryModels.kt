package com.gregmcgowan.fivesorganiser.match.summary

typealias MatchUiModelReducer = (MatchSummaryUiModel) -> MatchSummaryUiModel

sealed class MatchSummaryUiNavigationEvent {

    object Idle : MatchSummaryUiNavigationEvent()

    class ShowStartTimePicker(val hour: Int,
                              val minute: Int) : MatchSummaryUiNavigationEvent()

    class ShowEndTimePicker(val hour: Int,
                            val minute: Int) : MatchSummaryUiNavigationEvent()

    class ShowDatePicker(val year: Int,
                         val month: Int,
                         val date: Int) : MatchSummaryUiNavigationEvent()

    object CloseScreen : MatchSummaryUiNavigationEvent()

}

data class MatchSummaryUiModel(val title: String,
                               val loading: Boolean,
                               val showContent: Boolean,
                               val success: Boolean,
                               val errorMessage: String?,
                               val date: String,
                               val startTime: String,
                               val endTime: String,
                               val location: String,
                               val numberOfPLayers: String,
                               val confirmedPlayersCount: Int = 0,
                               val unknownPlayersCount: Int = 0,
                               val declinedPlayersCount: Int = 0)