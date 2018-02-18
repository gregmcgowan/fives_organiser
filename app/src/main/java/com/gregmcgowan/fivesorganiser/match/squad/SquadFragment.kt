package com.gregmcgowan.fivesorganiser.match.squad

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseFragment
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchNavigator
import com.gregmcgowan.fivesorganiser.match.squad.notinvitedplayers.NotInvitedPlayersListFragment

class SquadFragment : BaseFragment() {

    private lateinit var matchNavigator: MatchNavigator

    private val viewPager: ViewPager by find(R.id.match_squad_view_pager)
    private val tabLayout: TabLayout by find(R.id.match_squad_tab_layout)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.match_squad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            matchNavigator = ViewModelProviders.of(it).get(MatchActivityViewModel::class.java)
            tabLayout.setupWithViewPager(viewPager)
            viewPager.offscreenPageLimit = 1
            viewPager.adapter = SquadViewPagerAdapter(childFragmentManager, it)
        }
    }
}

class SquadViewPagerAdapter(fragmentManager: FragmentManager,
                            val context: Context) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NotInvitedPlayersListFragment()
            1 -> return InvitedPlayersFragment()
            else -> {

            }
        }
        throw IllegalStateException("No fragment")
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.squad_not_invited_players_tab_text)
            1 -> context.getString(R.string.squad_invited_players_tab_text)
            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}
