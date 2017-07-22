package com.gregmcgowan.fivesorganiser.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.gregmcgowan.fivesorganiser.Dependencies
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ViewBinder
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.find
import com.gregmcgowan.fivesorganiser.getApp
import com.gregmcgowan.fivesorganiser.players.PlayerListView
import com.gregmcgowan.fivesorganiser.players.PlayerListViewPresenter
import rx.Emitter
import rx.Observable

class MainActivity : AppCompatActivity(), MainContract.ParentView {

   // val rootView: ViewGroup by find<ViewGroup>(R.id.main_content)
    val content: View by find<View>(R.id.main_content)
    val progressBar: View by find<ProgressBar>(R.id.main_progress_bar)
    val playersView: View by find<View>(R.id.main_players_list_layout)
    val matchesView: View by find<View>(R.id.main_create_match_layout)
    val resultsView: View by find<View>(R.id.main_results_layout)
    val bottomNavigation: BottomNavigationView
            by find<BottomNavigationView>(R.id.main_bottom_navigation)

    //TODO maybe move to base activity
    val dependencies: Dependencies get() = getApp().dependencies

    lateinit var mainPresenter: MainContract.Presenter
    lateinit var menuItemObservable: Observable<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(find<Toolbar>(R.id.main_toolbar).value)

        //TODO maybe move to Dagger
        val mainViewPresenters = listOf<MainContract.MainViewPresenter>(
                PlayerListViewPresenter(
                        playerListView = PlayerListView(rootView = playersView,
                                                        context = this),
                        playersRepo = dependencies.playersRepo))

        mainPresenter = MainPresenter(
                mainParentView = this,
                mainViewPresenters = mainViewPresenters,
                authentication = dependencies.authentication)

        menuItemObservable = Observable.fromEmitter<Int>({
            emitter ->
            bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
                //TODO find out why this causes problems with the player list
                //TransitionManager.beginDelayedTransition(rootView)
                emitter.onNext(menuItem.itemId)
                true
            }

        }, Emitter.BackpressureMode.LATEST)
    }

    override fun onStart() {
        super.onStart()
        mainPresenter.startPresenting()
    }

    override fun onStop() {
        mainPresenter.stopPresenting()
        super.onStop()
    }

    override fun menuSelected(): Observable<Int> {
        return menuItemObservable
    }

    override var viewState: ViewState by ViewBinder {
        when (it) {
            is ViewState.Loading -> {
                showLoading()
            }
            is ViewState.Error -> {
                showError()
            }
            is ViewState.Success<*> -> {
                showSuccess(it)
            }
        }
    }

    private fun showLoading() {
        content.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showError() {
        content.visibility = View.GONE
        progressBar.visibility = View.GONE
        //TODO show error
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                .show()
    }

    private fun showSuccess(it: ViewState.Success<*>) {
        content.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        if (it.item is MainContract.MainModel) {
            when (it.item.menuIdToShow) {
                R.id.main_players_menu_item ->
                    showPlayersView()
                R.id.main_matches_menu_item ->
                    showMatchesView()
                R.id.main_results_menu_item ->
                    showResultsView()
                else -> {
                    //Do nothing
                }
            }
        }
    }

    fun showPlayersView() {
        matchesView.visibility = View.GONE
        playersView.visibility = View.VISIBLE
        resultsView.visibility = View.GONE
    }

    fun showMatchesView() {
        matchesView.visibility = View.VISIBLE
        playersView.visibility = View.GONE
        resultsView.visibility = View.GONE
    }

    fun showResultsView() {
        matchesView.visibility = View.GONE
        playersView.visibility = View.GONE
        resultsView.visibility = View.VISIBLE
    }

}
