package com.gregmcgowan.fivesorganiser.match.squad



data class InvitedPlayerItemModel(
        val playerName : String,
        var invitedPlayerStatus: InvitedPlayerStatus
)

enum class InvitedPlayerStatus {
    INVITED,
    UNSURE,
    CONFIRMED,
    DECLINED

}


