package com.gregmcgowan.fivesorganiser.match

sealed class MatchUiNavigationEvent {

    object Idle : MatchUiNavigationEvent()

    class ShowStartTimePicker(val hour: Int,
                              val minute: Int) : MatchUiNavigationEvent()

    class ShowEndTimePicker(val hour: Int,
                            val minute: Int) : MatchUiNavigationEvent()

    class ShowDatePicker(val year: Int,
                         val month: Int,
                         val date: Int) : MatchUiNavigationEvent()

    object CloseScreen : MatchUiNavigationEvent()

}

data class MatchUiModel(val title : String,
                        val loading: Boolean,
                        val showContent: Boolean,
                        val success: Boolean,
                        val errorMessage: String?,
                        val date: String,
                        val startTime: String,
                        val endTime : String,
                        val location: String,
                        val numberOfPLayers: String,
                        val confirmedPlayersCount : Int = 0,
                        val unknownPlayersCount : Int = 0,
                        val declinedPlayersCount: Int = 0)