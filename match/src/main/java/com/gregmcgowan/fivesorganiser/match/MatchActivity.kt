package com.gregmcgowan.fivesorganiser.match

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.match.MatchNavigationEvent.*
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsFragment
import com.gregmcgowan.fivesorganiser.match.squad.MatchSquadFragment
import dagger.hilt.android.AndroidEntryPoint

fun Context.createMatchIntent(matchEvent: MatchNavigationEvent): Intent {
    return Intent(this, MatchActivity::class.java)
            .apply {
                putExtra(MATCH_EVENT_EXTRA, matchEvent)
            }
}

const val MATCH_ID_INTENT_EXTRA = "match_id"
const val MATCH_EVENT_EXTRA = "MATCH_EVENT_EXTRA"

@AndroidEntryPoint
class MatchActivity : AppCompatActivity() {

    private val matchActivityViewModel: MatchActivityViewModel by viewModels()

    val matchEvent: MatchNavigationEvent
        get() = intent.getParcelableExtra(MATCH_EVENT_EXTRA)
                ?: throw IllegalStateException()

    private lateinit var matchFragment: MatchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_container)

        matchActivityViewModel
                .matchNavEvents
                .observeNonNull(this, this::handleNavEvent)
    }

    private fun handleNavEvent(event: MatchNavigationEvent) {
        when (event) {
            is StartNewMatchFlow -> {
                showFragment(MatchDetailsFragment())
            }
            is ShowMatchDetails -> {
                showFragment(MatchDetailsFragment.newInstance(event.matchId))
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
