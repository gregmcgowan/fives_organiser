package com.gregmcgowan.fivesorganiser.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreen.*
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiEvent
import com.gregmcgowan.fivesorganiser.main.MainContract.MainScreenUiEvent.MenuSelectedEvent
import com.gregmcgowan.fivesorganiser.matchList.MatchListUi
import com.gregmcgowan.fivesorganiser.matchList.MatchListUiPresenter
import com.gregmcgowan.fivesorganiser.playerList.PlayerListUi
import com.gregmcgowan.fivesorganiser.playerList.PlayerListUiPresenter
import com.gregmcgowan.fivesorganiser.core.setVisible
import io.reactivex.Observable
import io.reactivex.Observable.create

class MainActivity : BaseActivity(), MainContract.ParentUi {

    private val rootView: ViewGroup by find(R.id.main_content)
    private val content: View by find(R.id.main_content)
    private val progressBar: View by find(R.id.main_progress_bar)
    private val playersView: View by find(R.id.main_players_list_layout)
    private val matchesView: View by find(R.id.main_create_match_layout)
    private val resultsView: View by find(R.id.main_results_layout)
    private val bottomNavigation: BottomNavigationView by find(R.id.main_bottom_navigation)

    private lateinit var mainPresenter: MainContract.Presenter
    private lateinit var menuItemObservable: Observable<MainScreenUiEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(find<Toolbar>(R.id.main_toolbar).value)

        //TODO maybe move to Dagger
        val mainViewPresenters = listOf(
                PlayerListUiPresenter(
                        playerListUi = PlayerListUi(rootView = playersView, context = this),
                        playersRepo = dependencies.playersRepo),
                MatchListUiPresenter(
                        matchListUi = MatchListUi(rootView = matchesView, context = this),
                        matchRepo = dependencies.matchRepo,
                        dateTimeFormatter = ZonedDateTimeFormatter()
                )
        )

        mainPresenter = MainPresenter(
                mainParentUi = this,
                mainViewPresenters = mainViewPresenters,
                authentication = dependencies.authentication,
                mainScreenStore = MainScreenStateStore())

        lifecycle.addObserver(mainPresenter)

        menuItemObservable = create<MainScreenUiEvent>({ emitter ->
            bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.main_matches_menu_item -> emitter.onNext(MenuSelectedEvent(MatchesScreen))
                    R.id.main_players_menu_item -> emitter.onNext(MenuSelectedEvent(PlayersScreen))
                    R.id.main_results_menu_item -> emitter.onNext(MenuSelectedEvent(ResultsScreen))
                    else -> throw IllegalArgumentException()
                }
                true
            }
            emitter.setCancellable { bottomNavigation.setOnNavigationItemSelectedListener(null) }
        })
    }

    override fun menuSelected(): Observable<MainScreenUiEvent> {
        return menuItemObservable
    }

    override fun render(mainScreenUiModel: MainContract.MainScreenUiModel) {
        content.setVisible(mainScreenUiModel.showContent)
        progressBar.setVisible(mainScreenUiModel.showLoading)
        //TransitionManager.beginDelayedTransition(rootView)
        matchesView.setVisible(mainScreenUiModel.showMatchesView)
        playersView.setVisible(mainScreenUiModel.showPlayersView)
        resultsView.setVisible(mainScreenUiModel.showResultsView)
    }

}
