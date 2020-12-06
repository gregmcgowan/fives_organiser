package com.gregmcgowan.fivesorganiser.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListFragment
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.main.MainScreen.*
import com.gregmcgowan.fivesorganiser.main.results.ResultsFragment
import com.gregmcgowan.fivesorganiser.matchlist.MatchListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        mainViewModel
                .uiModelLiveData
                .observeNonNull(this) { render(it) }

        findViewById<BottomNavigationView>(R.id.main_bottom_navigation).setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main_matches_menu_item -> mainViewModel.handleMenuSelection(MatchesScreen)
                R.id.main_players_menu_item -> mainViewModel.handleMenuSelection(PlayersScreen)
                R.id.main_results_menu_item -> mainViewModel.handleMenuSelection(ResultsScreen)
                else -> throw IllegalArgumentException()
            }
            true
        }
    }

    private fun render(mainScreenUiModel: MainScreenUiModel) {
        findViewById<View>(R.id.main_progress_bar).setVisibleOrGone(mainScreenUiModel.showLoading)
        findViewById<View>(R.id.main_content_group).setVisibleOrGone(mainScreenUiModel.showContent)

        if (mainScreenUiModel.showContent) {
            when (mainScreenUiModel.screenToShow) {
                PlayersScreen -> showFragment(PlayerListFragment.PLAYER_LIST_FRAGMENT_TAG)
                MatchesScreen -> showFragment(MatchListFragment.MATCH_LIST_FRAGMENT_TAG)
                ResultsScreen -> showFragment(ResultsFragment.RESULTS_FRAGMENT_TAG)
            }
        }
    }

    private fun showFragment(tag: String) {
        TransitionManager.beginDelayedTransition(findViewById(R.id.main_content_container))
        supportFragmentManager.beginTransaction()
                .replace(R.id.main_content_container, getFragment(tag))
                .commit()
    }

    private fun getFragment(tag: String): Fragment {
        return supportFragmentManager.findFragmentByTag(tag).let {
            it ?: when (tag) {
                PlayerListFragment.PLAYER_LIST_FRAGMENT_TAG -> PlayerListFragment()
                MatchListFragment.MATCH_LIST_FRAGMENT_TAG -> MatchListFragment()
                ResultsFragment.RESULTS_FRAGMENT_TAG -> ResultsFragment()
                else -> {
                    throw IllegalArgumentException("Unknown fragment $tag")
                }
            }
        }
    }

}
