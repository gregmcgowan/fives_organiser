package com.gregmcgowan.fivesorganiser.match.squad

interface MatchSquadListPlayerContract {

    interface Presenter {

        fun bindModel(model: MatchSquadListItemUiModel)

        fun handleStatusUpdated(newStatus: MatchSquadListPlayerStatus)
    }

    interface View {

        fun setPresenter(presenter: Presenter)

        fun setName(name: String)

        fun setInvitedStatus(checked: Boolean)

        fun setDeclinedStatus(checked: Boolean)

        fun setUnknownStatus(checked: Boolean)

        fun setConfirmedStatus(checked: Boolean)

    }

}