package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.core.MatchTypesInfo
import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.data.R
import javax.inject.Inject

class MatchTypeHelper @Inject constructor(
        strings: Strings,
        matchTypes: MatchTypesInfo
) {

    private val map: Map<String, Int>

    init {
        val pairs = (matchTypes.minTeamSize..matchTypes.maxTeamSize)
                .map {
                    strings.getString(R.string.match_types_format, it) to it
                }
        @SuppressWarnings("SpreadOperator")
        map = mapOf(*pairs.toTypedArray())
    }


    fun getSquadSize(matchType: String): Int {
        return (map[matchType] ?: throw IllegalArgumentException("$matchType not found")) * 2
    }

    fun getMatchType(squadSize: Int): String {
        val teamSize = squadSize / 2

        for (entry in map.entries) {
            if (entry.value == teamSize) {
                return entry.key
            }
        }
        throw IllegalArgumentException("$squadSize not found")
    }

    fun getAllMatchTypes() : List<String> {
        return map.keys.toList()
    }

}