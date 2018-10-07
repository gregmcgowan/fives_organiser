package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.ShowSquad
import java.util.*
import javax.inject.Inject

class MatchActivityViewModel @Inject constructor(
        coroutineContexts: CoroutineContexts,
        matchEvent : MatchNavigationEvent
) : CoroutinesViewModel(coroutineContexts) {

    val matchNavEvents: LiveData<MatchNavigationEvent>
        get() = _matchNavEvents

    private val _matchNavEvents = MutableLiveData<MatchNavigationEvent>()
    private val stack = Stack<MatchNavigationEvent>()

    init {
        handle(matchEvent)
    }

    fun upButtonPressed() {
        upOrBackPressed()
    }

    fun backButtonPressed() {
        upButtonPressed()
    }

    fun nextInNewMatchFlow(matchId: String) {
        // TODO implement the proper flow here
        // 1. time and location
        // 2. pick squad
        // 3. send invites
        // ??? back to summary or show empty teams
        stack.clear()
        handle(ShowSquad(matchId))
    }

    private fun upOrBackPressed() {
        stack.pop()

        if (stack.size == 0) {
            _matchNavEvents.value = MatchNavigationEvent.CloseScreen
        } else {
            _matchNavEvents.value = stack.peek()
        }
    }

    fun showTimeAndLocationScreen(matchId: String) {
        handle(MatchNavigationEvent.ShowMatchDetails(matchId))
    }

    private fun handle(event: MatchNavigationEvent) {
        stack.push(event)
        _matchNavEvents.value = event
    }

}
