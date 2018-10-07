package com.gregmcgowan.fivesorganiser.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.BottomNavigationView
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.View
import com.gregmcgowan.fivesorgainser.playerlist.PlayerListFragment
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import com.gregmcgowan.fivesorganiser.main.MainScreen.*
import com.gregmcgowan.fivesorganiser.matchlist.MatchListFragment
import com.gregmcgowan.fivesorganiser.main.results.ResultsFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel

    private val content: Group by find(R.id.main_content_group)
    private val progressBar: View by find(R.id.main_progress_bar)
    private val bottomNavigation: BottomNavigationView by find(R.id.main_bottom_navigation)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(find<Toolbar>(R.id.main_toolbar).value)

        AndroidInjection.inject(this)

        mainViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainViewModel::class.java)

        mainViewModel
                .uiModelLiveData
                .observeNonNull(this) { render(it) }

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main_matches_menu_item -> mainViewModel.handleMenuSelection(MatchesScreen)
                R.id.main_players_menu_item -> mainViewModel.handleMenuSelection(PlayersScreen)
                R.id.main_results_menu_item -> mainViewModel.handleMenuSelection(ResultsScreen)
                else -> throw IllegalArgumentException()
            }
            true
        }

        mainViewModel.onViewCreated()
    }

    private fun render(mainScreenUiModel: MainScreenUiModel) {
        progressBar.setVisibleOrGone(mainScreenUiModel.showLoading)
        content.setVisibleOrGone(mainScreenUiModel.showContent)

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
