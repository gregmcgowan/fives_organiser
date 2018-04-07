package com.gregmcgowan.fivesorganiser.match.inviteplayers

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.match.PlayerAndMatchStatus
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.getString
import javax.inject.Inject

class InvitePlayersUiModelMapper @Inject constructor(private val strings: Strings) {

    fun map(playerAndMatchList: List<PlayerAndMatchStatus>): List<InvitePlayerListItemModel> {
        return playerAndMatchList
                .mapTo(mutableListOf()) { playerAndMatchStatus ->
                    InvitePlayerListItemModel(
                            playerAndMatchStatus.player.playerId,
                            playerAndMatchStatus.player.name,
                            mapStatus(playerAndMatchStatus.matchSquadStatus)
                    )
                }
    }


    private fun mapStatus(matchSquadStatus: PlayerMatchSquadStatus): Int {
        return strings.getStringArray(R.array.player_invitation_status)
                .indexOf(matchSquadStatus.getString())
    }

}