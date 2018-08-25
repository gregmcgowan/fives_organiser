package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadFragment
import com.gregmcgowan.fivesorganiser.match.timelocation.MatchTimeAndLocationFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

fun Context.createMatchIntent(matchEvent : MatchNavigationEvent): Intent {
    return Intent(this, MatchActivity::class.java)
            .apply {
                putExtra(MATCH_EVENT_EXTRA, matchEvent)
            }
}

const val MATCH_ID_INTENT_EXTRA = "match_id"
const val MATCH_EVENT_EXTRA = "MATCH_EVENT_EXTRA"

class MatchActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var matchActivityViewModel: MatchActivityViewModel

    val matchEvent: MatchNavigationEvent
        get() = intent.getParcelableExtra(MATCH_EVENT_EXTRA)
                ?: throw IllegalStateException()

    private lateinit var matchFragment: MatchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_container)

        AndroidInjection.inject(this)

        matchActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MatchActivityViewModel::class.java)

        matchActivityViewModel
                .matchNavEvents
                .observeNonNull(this, this::handleNavEvent)
    }

    private fun handleNavEvent(event: MatchNavigationEvent) {
        when (event) {
            is StartNewMatchFlow -> {
                showFragment(MatchTimeAndLocationFragment())
            }
            is ShowMatchTimeAndLocation -> {
                showFragment(MatchTimeAndLocationFragment.newInstance(event.matchId))
            }
            is ShowSquad -> {
                showFragment(MatchSquadFragment.newInstance(event.matchId))
            }
            is CloseScreen -> {
                finish()
            }
        }
    }

    private fun showFragment(matchFragment: Fragment) {
        if (matchFragment is MatchFragment) {
            this.matchFragment = matchFragment
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.match_fragment_container, matchFragment)
                .commit()
    }

    override fun onBackPressed() {
        if (!matchFragment.consumeBackPress()) {
            super.onBackPressed()
        }
    }
}
