package com.gregmcgowan.fivesorganiser.match.inviteplayers

import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.data.player.Player
import javax.inject.Inject

class InvitePlayersUiModelMapper @Inject constructor(private val strings: Strings) {

    fun map(notInvitedPlayersList: List<Player>): List<InvitePlayerListItemModel> {
        return notInvitedPlayersList
                .mapTo(mutableListOf()) { playerAndMatchStatus ->
                    InvitePlayerListItemModel(
                            playerAndMatchStatus.playerId,
                            playerAndMatchStatus.name,
                            false
                    )
                }.toMutableList()
    }

//
//    private fun mapStatus(matchSquadStatus: PlayerMatchSquadStatus): Int {
//        return strings.getStringArray(R.array.player_invitation_status)
//                .indexOf(matchSquadStatus.getString())
//    }

}