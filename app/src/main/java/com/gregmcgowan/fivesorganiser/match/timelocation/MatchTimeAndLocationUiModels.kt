package com.gregmcgowan.fivesorganiser.match.timelocation


data class MatchTimeAndLocationUiModel(
        val loading: Boolean,
        val showErrorState: Boolean,
        val showContent: Boolean,
        val date: String,
        val startTime: String,
        val endTime: String,
        val location: String,
        val showCreateSquadButton: Boolean
)


sealed class MatchTimeAndLocationNavEvent {

    object Idle : MatchTimeAndLocationNavEvent()

    class ShowStartTimePicker(val hour: Int,
                              val minute: Int) : MatchTimeAndLocationNavEvent()

    class ShowEndTimePicker(val hour: Int,
                            val minute: Int) : MatchTimeAndLocationNavEvent()

    class ShowDatePicker(val year: Int,
                         val month: Int,
                         val date: Int) : MatchTimeAndLocationNavEvent()

    class GoToSquadScreenButtonPressed(val matchId: String) : MatchTimeAndLocationNavEvent()

    object BackPressed : MatchTimeAndLocationNavEvent()

}