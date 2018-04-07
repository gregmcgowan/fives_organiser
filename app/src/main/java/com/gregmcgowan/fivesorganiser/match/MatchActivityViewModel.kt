package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import javax.inject.Inject

class MatchActivityViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val matchId: String?,
        private val matchStateHolder: MatchStateHolder,
        private val matchOrchestrator: MatchOrchestrator
) : CoroutinesViewModel(coroutineContext) {

    private val matchNavEvents = MutableLiveData<MatchActivityNavigationEvent>()

    init {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowLoading

        runOnBackgroundAndUpdateOnUI({
            if (matchId == null) {
                matchStateHolder.createOrRestoreMatch()
            } else {
                matchStateHolder.match = matchOrchestrator.getMatch(matchId)
            }
        }, {
            matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
        })
    }

    fun navigationEvents(): LiveData<MatchActivityNavigationEvent> {
        return matchNavEvents
    }

    fun showMatchSummary() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
    }

    fun showMatchSquad() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowSquad
    }

    fun showTeams() {
        matchNavEvents.value = MatchActivityNavigationEvent.ShowTeams
    }

    fun upButtonPressed() {
        if (matchNavEvents.value != MatchActivityNavigationEvent.ShowSummary) {
            matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
        } else {
            matchNavEvents.value = MatchActivityNavigationEvent.CloseScreen
        }
    }

    fun backButtonPressed() {
        if (matchNavEvents.value != MatchActivityNavigationEvent.ShowSummary) {
            matchNavEvents.value = MatchActivityNavigationEvent.ShowSummary
        } else {
            matchNavEvents.value = MatchActivityNavigationEvent.CloseScreen
        }
    }

    fun saveButtonPressed() {
        //TODO validation???
        runOnBackgroundAndUpdateOnUI({
            if (matchId == null) {
                matchOrchestrator.createMatch(
                        matchStateHolder.match.start,
                        matchStateHolder.match.end,
                        matchStateHolder.match.squad.expectedSize,
                        matchStateHolder.match.location
                )
            } else {
                matchOrchestrator.saveMatch(matchStateHolder.match)
            }
        }, {
            matchNavEvents.value = MatchActivityNavigationEvent.CloseScreen
        })
    }
}


sealed class MatchActivityNavigationEvent {
    object CloseScreen : MatchActivityNavigationEvent()
    object ShowLoading : MatchActivityNavigationEvent()
    object ShowSummary : MatchActivityNavigationEvent()
    object ShowSquad : MatchActivityNavigationEvent()
    object ShowTeams : MatchActivityNavigationEvent()
}
