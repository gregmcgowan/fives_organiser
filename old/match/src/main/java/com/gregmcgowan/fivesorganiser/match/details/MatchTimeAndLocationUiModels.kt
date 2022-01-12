package com.gregmcgowan.fivesorganiser.match.details


data class MatchDetailsUiModel(
        val loading: Boolean,
        val showErrorState: Boolean,
        val showContent: Boolean,
        val date: String,
        val startTime: String,
        val endTime: String,
        val location: String,
        val matchTypeOptions : List<String>,
        val selectedMatchTypeIndex : Int = -1,
        val showCreateSquadButton: Boolean
)


sealed class MatchDetailsNavEvent {

    object Idle : MatchDetailsNavEvent()

    class ShowStartTimePicker(val hour: Int,
                              val minute: Int) : MatchDetailsNavEvent()

    class ShowEndTimePicker(val hour: Int,
                            val minute: Int) : MatchDetailsNavEvent()

    class ShowDatePicker(val year: Int,
                         val month: Int,
                         val date: Int) : MatchDetailsNavEvent()

    class GoToSquadScreenButtonPressed(val matchId: String) : MatchDetailsNavEvent()

    object BackPressed : MatchDetailsNavEvent()

}