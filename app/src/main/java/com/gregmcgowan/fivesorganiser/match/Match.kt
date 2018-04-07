package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import org.threeten.bp.ZonedDateTime

data class Match(val matchId: String,
                 val location: String,
                 val start: ZonedDateTime,
                 val end: ZonedDateTime,
                 val squad: Squad
)

data class Squad(
        val expectedSize: Long,
        val playerAndStatuses: List<PlayerAndMatchStatus> = emptyList()
)

data class PlayerAndMatchStatus(
        val player: Player,
        val matchSquadStatus: PlayerMatchSquadStatus
)

fun Squad.getPlayersWithStatus(matchSquadStatus: PlayerMatchSquadStatus): List<String> {
    return this.playerAndStatuses
            .filter { playerAndMatchStatus ->
                playerAndMatchStatus.matchSquadStatus == matchSquadStatus
            }.map { playerAndMatchStatus ->
                playerAndMatchStatus.player.playerId
            }
}

fun Squad.updatePlayerStatus(player: Player,
                             matchSquadStatus: PlayerMatchSquadStatus): Squad {
    val playerIndex = getPlayerIndex(playerId = player.playerId)
    val toMutableList = this.playerAndStatuses
            .toMutableList()
    if (playerIndex == -1) {
        toMutableList.add(PlayerAndMatchStatus(player, matchSquadStatus))
    } else {
        toMutableList[playerIndex] = PlayerAndMatchStatus(player, matchSquadStatus)
    }
    return this.copy(playerAndStatuses = toMutableList)
}

fun Squad.getPlayerIndex(playerId: String) =
        this.playerAndStatuses.indexOfFirst { playerAndMatchStatus ->
            playerAndMatchStatus.player.playerId == playerId
        }

fun List<PlayerAndMatchStatus>.getPlayerIndex(playerId : String) : Int {
    return this.indexOfFirst { it.player.playerId == playerId }
}

enum class PlayerMatchSquadStatus {
    NO_STATUS,
    INVITED,
    DECLINED,
    UNSURE,
    CONFIRMED
}


fun String.toPlayerMatchSquadStatus(): PlayerMatchSquadStatus {
    return when (this) {
        "Invited" -> PlayerMatchSquadStatus.INVITED
        "Confirmed" -> PlayerMatchSquadStatus.CONFIRMED
        "Unsure" -> PlayerMatchSquadStatus.UNSURE
        "Not Available" -> PlayerMatchSquadStatus.DECLINED
        else -> PlayerMatchSquadStatus.NO_STATUS
    }
}

fun PlayerMatchSquadStatus.getString()  : String {
    return when (this) {
        PlayerMatchSquadStatus.INVITED -> "Invited"
        PlayerMatchSquadStatus.CONFIRMED -> "Confirmed"
        PlayerMatchSquadStatus.UNSURE -> "Unsure"
        PlayerMatchSquadStatus.DECLINED -> "Not Available"
        PlayerMatchSquadStatus.NO_STATUS -> ""
    }

}


