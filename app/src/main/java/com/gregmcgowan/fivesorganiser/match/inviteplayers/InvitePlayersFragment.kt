package com.gregmcgowan.fivesorganiser.match.inviteplayers

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import javax.inject.Inject

class InvitePlayersFragment : BaseFragment() {

    private lateinit var uninvitedPlayerList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var uninvitedPlayersViewModel: InvitePlayersViewModel
    private lateinit var navigationViewModel: MatchActivityViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val notInvitedPlayersListAdapter = InvitePlayersListAdapter()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.match_squad_invite_players_layout, container, false)
    }

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uninvitedPlayerList = find<RecyclerView>(R.id.match_squad_not_invited_list).value
        progressBar = find<ProgressBar>(R.id.match_squad_not_invited_progress_bar).value
        uninvitedPlayerList.adapter = notInvitedPlayersListAdapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            val matchId = it.intent?.getStringExtra(MATCH_ID_INTENT_EXTRA)
                    ?: throw java.lang.IllegalArgumentException()

            DaggerInvitePlayersComponent.builder()
                    .appComponent(appComponent)
                    .matchId(matchId)
                    .build()
                    .inject(this)

            uninvitedPlayersViewModel = ViewModelProviders
                    .of(this, viewModelFactory)
                    .get(InvitePlayersViewModel::class.java)

            navigationViewModel = ViewModelProviders
                    .of(it)
                    .get(MatchActivityViewModel::class.java)

        }
        uninvitedPlayersViewModel
                .uiModel()
                .observeNonNull(this, this::render)

        uninvitedPlayersViewModel.onViewShown()
    }

    private fun render(uninvitedPlayersUiModel: InvitePlayersUiModel) {
        progressBar.setVisible(uninvitedPlayersUiModel.showLoading)
        uninvitedPlayerList.setVisible(uninvitedPlayersUiModel.showContent)
        notInvitedPlayersListAdapter.setUninvitedPlayers(uninvitedPlayersUiModel.invitePlayersList)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.add_edit_match, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.save_match -> uninvitedPlayersViewModel.saveAndExit()
                R.id.home -> navigationViewModel.upButtonPressed()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}


