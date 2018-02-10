package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.match.squad.SquadFragment
import com.gregmcgowan.fivesorganiser.match.summary.MatchSummaryFragment

fun Context.editMatchIntent(matchId: String): Intent {
    return Intent(this, MatchActivity::class.java)
            .apply {
                putExtra(MATCH_ID_INTENT_EXTRA, matchId)
            }
}

fun Context.createMatchIntent(): Intent {
    return Intent(this, MatchActivity::class.java)
}

const val MATCH_ID_INTENT_EXTRA = "match_id"

class MatchActivity : BaseActivity() {

    private lateinit var matchActivityViewModel: MatchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_container)

        val toolbar = find<Toolbar>(R.id.match_toolbar).value
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        matchActivityViewModel = ViewModelProviders.of(this)
                .get(MatchActivityViewModel::class.java)

        matchActivityViewModel
                .navigationEvents()
                .observeNonNull(this, this::handleNavEvent)
    }

    private fun handleNavEvent(event: MatchActivityNavigationEvent) {
        when (event) {
            MatchActivityNavigationEvent.ShowSummary -> showMatchSummary()
            MatchActivityNavigationEvent.ShowSquad -> showSquad()
            MatchActivityNavigationEvent.UpButtonPressed -> handleUpButtonPressed()
            else -> {
                //
            }
        }
    }

    private fun handleUpButtonPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun showSquad() {
        supportFragmentManager.beginTransaction()
                .add(R.id.match_fragment_container, SquadFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun showMatchSummary() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.match_fragment_container, MatchSummaryFragment())
                .commit()
    }


}
