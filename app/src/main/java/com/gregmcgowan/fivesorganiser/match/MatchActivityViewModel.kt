package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MatchActivityViewModel : ViewModel(), MatchNavigator {

    private val matchNavEvents = MutableLiveData<MatchActivityNavigationEvent>()

    init {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
    }

    fun navigationEvents(): LiveData<MatchActivityNavigationEvent> {
        return matchNavEvents
    }

    override fun showMatchSummary() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
    }

    override fun showMatchSquad() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowSquad
    }

    override fun showTeams() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowTeams
    }

    override fun upButtonPressed() {
        if(matchNavEvents.value != MatchActivityNavigationEvent.ShowSummary) {
            matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
        } else {
            matchNavEvents.value = MatchActivityNavigationEvent.CloseScreen
        }
    }

    override fun backButtonPressed() {
        if(matchNavEvents.value != MatchActivityNavigationEvent.ShowSummary) {
            matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
        } else {
            matchNavEvents.value = MatchActivityNavigationEvent.CloseScreen
        }
    }

}


sealed class MatchActivityNavigationEvent {
    object CloseScreen : MatchActivityNavigationEvent()
    object ShowSummary : MatchActivityNavigationEvent()
    object ShowSquad : MatchActivityNavigationEvent()
    object ShowTeams : MatchActivityNavigationEvent()
}