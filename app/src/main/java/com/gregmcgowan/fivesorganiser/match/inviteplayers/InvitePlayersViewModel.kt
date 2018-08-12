package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.match.PlayerMatchSquadStatus
import javax.inject.Inject

class InvitePlayersViewModel @Inject constructor(
        coroutineContext: CoroutineContexts,
        private val invitePlayersUiModelMapper: InvitePlayersUiModelMapper,
        private val invitePlayersOrchestrator: InvitePlayersOrchestrator

) : CoroutinesViewModel(coroutineContext) {

    private val notInvitedPlayersUiModel = MutableLiveData<InvitePlayersUiModel>()
    private lateinit var allPlayers: List<Player>
    private val playersToInvite = mutableListOf<Player>()

    init {
        notInvitedPlayersUiModel.value = InvitePlayersUiModel(
                showLoading = true,
                showContent = false,
                invitePlayersList = emptyList()
        )

        runOnBackgroundAndUpdateOnUI({
            allPlayers = invitePlayersOrchestrator.getUninvitedPlayers()
            mapToUiModel()
        }, { uiModel -> notInvitedPlayersUiModel.value = uiModel })
    }

    fun uiModel(): LiveData<InvitePlayersUiModel> {
        return notInvitedPlayersUiModel
    }

    fun onViewShown() {

    }

    private fun mapToUiModel(): InvitePlayersUiModel {

//        matchTypeOptions = matchTypes,
//        selectedMatchTypeIndex = selectedMatchType,
//        confirmedPlayersCount = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.CONFIRMED).size,
//        declinedPlayersCount = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.DECLINED).size,
//        unknownPlayersCount = match.squad.getPlayersWithStatus(PlayerMatchSquadStatus.UNSURE).size

        return InvitePlayersUiModel(
                showLoading = false,
                showContent = true,
                invitePlayersList = invitePlayersUiModelMapper.map(allPlayers))
    }


//    private fun getSelectedMatchTypeIndex(match: Match, matchTypes: List<String>): Int {
//        val numberOfPlayers: Int
//        // TODO maybe default to last selected size
//        if (match.squad.expectedSize == 0L) {
//            numberOfPlayers = DEFAULT_NO_OF_PLAYERS.toInt()
//        } else {
//            numberOfPlayers = match.squad.expectedSize.toInt()
//        }
//        val matchType = matchTypeHelper.getMatchType(numberOfPlayers)
//        return matchTypes.indexOf(matchType)
//    }

//    internal fun matchTypeUpdated(match: Match): MatchUiModelReducer = { model ->
//        val matchTypes = matchTypeHelper.getAllMatchTypes()
//        val selectedMatchType = getSelectedMatchTypeIndex(match, matchTypes)
//        model.copy(
//                selectedMatchTypeIndex = selectedMatchType
//        )
//    }


    fun handlePlayerStatusChanged(playerId: String, inviteStatus: Boolean) {
        //Timber.d("$playerId changed to $status")
        if (inviteStatus) {
            val player = allPlayers.find {
                it.playerId == playerId
            } ?: throw IllegalArgumentException()

            playersToInvite.add(player)
        } else {
            playersToInvite.removeAt(playersToInvite.indexOfFirst { it.playerId == playerId })
        }
    }

    fun matchTypeSelected(itemAtPosition: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updatePlayerStatus(player: Player,
                           status: PlayerMatchSquadStatus) {
        //match = match.copy(squad = match.squad.updatePlayerStatus(player, status))
    }

}