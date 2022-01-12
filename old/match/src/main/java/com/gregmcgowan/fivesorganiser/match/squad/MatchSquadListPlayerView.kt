package com.gregmcgowan.fivesorganiser.match.squad

import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.match_squad_list_item.*
import javax.inject.Inject

class MatchSquadListPlayerView @Inject constructor(
        private val rootView: View
) : MatchSquadListPlayerContract.View, LayoutContainer {

    private val onClickListener = View.OnClickListener { presenter.handleStatusUpdated(it.tag as MatchSquadListPlayerStatus) }

    private lateinit var presenter: MatchSquadListPlayerContract.Presenter

    init {
        match_squad_player_invited_radio_button.tag = MatchSquadListPlayerStatus.INVITED_SELECTED
        match_squad_player_invited_radio_button.setOnClickListener(onClickListener)
        match_squad_player_declined_radio_button.tag = MatchSquadListPlayerStatus.DECLINED_SELECTED
        match_squad_player_declined_radio_button.setOnClickListener(onClickListener)
        match_squad_player_unknown_radio_button.tag = MatchSquadListPlayerStatus.UNKNOWN_SELECTED
        match_squad_player_unknown_radio_button.setOnClickListener(onClickListener)
        match_squad_player_confirmed_radio_button.tag = MatchSquadListPlayerStatus.CONFIRMED_SELECTED
        match_squad_player_confirmed_radio_button.setOnClickListener(onClickListener)
    }

    override fun setName(name: String) {
        match_squad_player_name.text = name
    }

    override fun setInvitedStatus(checked: Boolean) {
        match_squad_player_invited_radio_button.isSelected = checked
    }

    override fun setDeclinedStatus(checked: Boolean) {
        match_squad_player_declined_radio_button.isSelected = checked
    }

    override fun setUnknownStatus(checked: Boolean) {
        match_squad_player_unknown_radio_button.isSelected = checked
    }

    override fun setConfirmedStatus(checked: Boolean) {
        match_squad_player_confirmed_radio_button.isSelected = checked
    }

    override fun setPresenter(presenter: MatchSquadListPlayerContract.Presenter) {
        this.presenter = presenter
    }

    override val containerView: View?
        get() = rootView
}