package com.gregmcgowan.fivesorganiser.match

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.ShowSquad
import java.util.*

class MatchActivityViewModel @ViewModelInject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        matchEvent : MatchNavigationEvent
) : CoroutinesViewModel(coroutineDispatchers) {

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
