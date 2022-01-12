package com.gregmcgowan.fivesorganiser.match.squad

import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadListPlayerStatus.*
import javax.inject.Inject

class MatchSquadListPlayerPresenter @Inject constructor(
        private val view: MatchSquadListPlayerContract.View,
        private val interactions: MatchSquadListInteractions
) : MatchSquadListPlayerContract.Presenter {

    private lateinit var model: MatchSquadListItemUiModel

    init {
        view.setPresenter(this)
    }

    override fun bindModel(model: MatchSquadListItemUiModel) {
        this.model = model
        view.setName(model.playerName)
        setNewStatus(model.status)
    }

    override fun handleStatusUpdated(newStatus: MatchSquadListPlayerStatus) {
        val existingStatus = model.status
        if (newStatus != existingStatus) {
            setNewStatus(newStatus)
            model.status = newStatus
            interactions.playerUpdated(model.playerId, newStatus)
        } else {
            when (existingStatus) {
                INVITED_SELECTED -> view.setInvitedStatus(false)
                DECLINED_SELECTED -> view.setDeclinedStatus(false)
                UNKNOWN_SELECTED -> view.setUnknownStatus(false)
                CONFIRMED_SELECTED -> view.setConfirmedStatus(false)
                NOT_SELECTED -> {
                    //nope should not happen
                }
            }
            model.status = UNKNOWN_SELECTED
            interactions.playerUpdated(model.playerId, UNKNOWN_SELECTED)
        }

    }

    private fun setNewStatus(newStatus: MatchSquadListPlayerStatus) {
        view.setInvitedStatus(newStatus == INVITED_SELECTED)
        view.setDeclinedStatus(newStatus == DECLINED_SELECTED)
        view.setUnknownStatus(newStatus == UNKNOWN_SELECTED)
        view.setConfirmedStatus(newStatus == CONFIRMED_SELECTED)
    }
}