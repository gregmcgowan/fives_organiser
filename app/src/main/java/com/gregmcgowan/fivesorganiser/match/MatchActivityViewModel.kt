package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Parcelable
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import kotlinx.android.parcel.Parcelize
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
            _matchNavEvents.value = CloseScreen
        } else {
            _matchNavEvents.value = stack.peek()
        }
    }

    fun showTimeAndLocationScreen(matchId: String) {
        handle(ShowMatchDetails(matchId))
    }

    private fun handle(event: MatchNavigationEvent) {
        stack.push(event)
        _matchNavEvents.value = event
    }

}


sealed class MatchNavigationEvent : Parcelable {

    @Parcelize
    object Idle : MatchNavigationEvent()

    @Parcelize
    class ShowMatchDetails(val matchId: String) : MatchNavigationEvent()

    @Parcelize
    class ShowSquad(val matchId : String) : MatchNavigationEvent()

    @Parcelize
    object StartNewMatchFlow : MatchNavigationEvent()

    @Parcelize
    object CloseScreen : MatchNavigationEvent()

}
