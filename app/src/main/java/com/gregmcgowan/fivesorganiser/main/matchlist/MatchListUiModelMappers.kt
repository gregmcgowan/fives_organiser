package com.gregmcgowan.fivesorganiser.main.matchlist

import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.match.Match
import com.gregmcgowan.fivesorganiser.match.MatchTypeHelper
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import com.gregmcgowan.fivesorganiser.match.getPlayersWithStatus
import javax.inject.Inject

class MatchListUiModelMappers @Inject constructor(
        private val strings: Strings,
        private val dateTimeFormatter: ZonedDateTimeFormatter,
        private val matchTypeHelper: MatchTypeHelper
) {

    fun map(matches: List<Match>): MatchListUiModel =
            MatchListUiModel(
                    showEmptyView = matches.isEmpty(),
                    showProgressBar = false,
                    showList = true,
                    matches = matches.map(this::mapMatch),
                    emptyMessage = getErrorMessage(matches)
            )


    private fun mapMatch(match: Match): MatchListItemUiModel {
        val expectedSquadSize = match.squad.expectedSize.toInt()
        val numberOfConfirmedPlayers = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.CONFIRMED).size

        return MatchListItemUiModel(
                matchId = match.matchId,
                matchType = matchTypeHelper.getMatchType(match.squad.expectedSize.toInt()),
                dateAndTime = getDateAndTime(match),
                location = match.location,
                squadStatus = getSquadStatus(expectedSquadSize - numberOfConfirmedPlayers)
        )
    }

    private fun getDateAndTime(match: Match): String = strings.getString(
            R.string.match_list_summary_format,
            dateTimeFormatter.formatDate(match.start),
            dateTimeFormatter.formatTime(match.start),
            dateTimeFormatter.formatTime(match.end)
    )

    private fun getSquadStatus(playersNeeded: Int): String = if (playersNeeded == 0) {
        strings.getString(R.string.match_list_full_squad_status)
    } else {
        strings.getString(R.string.match_list_more_players_needed_squad_status, playersNeeded)
    }

    private fun getErrorMessage(matches: List<Match>): String? = if (matches.isEmpty()) {
        strings.getString(R.string.match_list_empty_text)
    } else {
        null
    }

}



