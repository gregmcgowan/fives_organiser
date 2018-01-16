package com.gregmcgowan.fivesorganiser.match

sealed class MatchUiNavigationEvent {

    object Idle : MatchUiNavigationEvent()

    class ShowTimePicker(val hour: Int,
                         val minute: Int) : MatchUiNavigationEvent()

    class ShowDatePicker(val year: Int,
                         val month: Int,
                         val date: Int) : MatchUiNavigationEvent()

    object CloseScreen : MatchUiNavigationEvent()

}

data class MatchUiModel(val loading: Boolean,
                        val showContent: Boolean,
                        val success: Boolean,
                        val errorMessage: String?,
                        val date: String,
                        val time: String,
                        val location: String,
                        val numberOfPLayers: String)