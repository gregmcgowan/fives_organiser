package com.gregmcgowan.fivesorganiser.match.squad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseFragment

class InvitedPlayersFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.match_squad_invited_players, container, false)
    }
}