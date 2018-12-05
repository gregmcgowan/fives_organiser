package com.gregmcgowan.fivesorganiser.matchlist

import com.gregmcgowan.fivesorganiser.core.Strings
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.match.Match
import javax.inject.Inject

class MatchListUiModelMappers @Inject constructor(
        private val strings: Strings,
        private val listItemMapper: MatchListItemUiModelMapper
) {

    fun map(existingUiModel: MatchListUiModel, update: DataUpdate<Match>): MatchListUiModel {
        val updatedMatchListModels = updateMatches(existingUiModel.matches, update.changes)

        val matchesExist = updatedMatchListModels.isNotEmpty()

        return MatchListUiModel(
                showEmptyView = !matchesExist,
                showProgressBar = false,
                showList = matchesExist,
                matches = updatedMatchListModels,
                emptyMessage = getErrorMessage(updatedMatchListModels)
        )
    }

    private fun updateMatches(existingMatchUiModels: List<MatchListItemUiModel>,
                              updates: List<DataChange<Match>>): List<MatchListItemUiModel> {
        val newList = existingMatchUiModels.toMutableList()
        updates.forEach { update ->
            val match = update.data
            val findPlayerIndex = existingMatchUiModels.indexOfFirst { it.matchId == match.matchId }
            when (update.type) {
                DataChangeType.Added -> {
                    if (findPlayerIndex == -1) {
                        newList.add(listItemMapper.map(match))
                    }
                }
                DataChangeType.Modified -> {
                    if (findPlayerIndex != -1) {
                        newList[findPlayerIndex] = listItemMapper.map(match)
                    }
                }
                DataChangeType.Removed -> {
                    if (findPlayerIndex != -1) {
                        newList.removeAt(findPlayerIndex)
                    }
                }
            }
        }

        return newList
    }

    private fun getErrorMessage(matches: List<MatchListItemUiModel>): String? = if (matches.isEmpty()) {
        strings.getString(R.string.match_list_empty_text)
    } else {
        null
    }


}



