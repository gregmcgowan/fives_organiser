package com.gregmcgowan.fivesorganiser.match

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
