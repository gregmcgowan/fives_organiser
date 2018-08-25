package com.gregmcgowan.fivesorganiser.match.squad

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import javax.inject.Inject

class MatchSquadListPlayerView @Inject constructor(rootView: View) : MatchSquadListPlayerContract.View {

    private val playerName: TextView by rootView.find(R.id.match_squad_player_name)
    private val playerInvitedButton: ImageView by rootView.find(R.id.match_squad_player_invited_radio_button)
    private val playerDeclinedButton: ImageView by rootView.find(R.id.match_squad_player_declined_radio_button)
    private val playerUnknownButton: ImageView by rootView.find(R.id.match_squad_player_unknown_radio_button)
    private val playerConfirmedButton: ImageView by rootView.find(R.id.match_squad_player_confirmed_radio_button)

    private val onClickListener = View.OnClickListener { presenter.handleStatusUpdated(it.tag as MatchSquadListPlayerStatus) }

    private lateinit var presenter: MatchSquadListPlayerContract.Presenter

    init {
        playerInvitedButton.tag = MatchSquadListPlayerStatus.INVITED_SELECTED
        playerInvitedButton.setOnClickListener(onClickListener)
        playerDeclinedButton.tag = MatchSquadListPlayerStatus.DECLINED_SELECTED
        playerDeclinedButton.setOnClickListener(onClickListener)
        playerUnknownButton.tag = MatchSquadListPlayerStatus.UNKNOWN_SELECTED
        playerUnknownButton.setOnClickListener(onClickListener)
        playerConfirmedButton.tag = MatchSquadListPlayerStatus.CONFIRMED_SELECTED
        playerConfirmedButton.setOnClickListener(onClickListener)
    }

    override fun setName(name: String) {
        playerName.text = name
    }

    override fun setInvitedStatus(checked: Boolean) {
        playerInvitedButton.isSelected = checked
    }

    override fun setDeclinedStatus(checked: Boolean) {
        playerDeclinedButton.isSelected = checked
    }

    override fun setUnknownStatus(checked: Boolean) {
        playerUnknownButton.isSelected = checked
    }

    override fun setConfirmedStatus(checked: Boolean) {
        playerConfirmedButton.isSelected = checked
    }

    override fun setPresenter(presenter: MatchSquadListPlayerContract.Presenter) {
        this.presenter = presenter
    }
}