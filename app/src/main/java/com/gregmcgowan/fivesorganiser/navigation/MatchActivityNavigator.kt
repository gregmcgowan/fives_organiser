package com.gregmcgowan.fivesorganiser.navigation

import android.app.Activity
import com.gregmcgowan.fivesorganiser.match.createMatchIntent
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent
import com.gregmcgowan.fivesorganiser.match.MatchNavigator
import javax.inject.Inject

class MatchActivityNavigator @Inject constructor(private val activity: Activity) : MatchNavigator {

    override fun handleEvent(navEvent: MatchNavigationEvent) {
        when (navEvent) {
            is MatchNavigationEvent.Idle -> {
                //Do nothing
            }
            else -> {
                activity.startActivity(activity.createMatchIntent(navEvent))
            }
        }
    }
}
