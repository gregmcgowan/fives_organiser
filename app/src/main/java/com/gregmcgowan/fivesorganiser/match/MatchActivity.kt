package com.gregmcgowan.fivesorganiser.match

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.match.MatchActivityNavigationEvent.*
import com.gregmcgowan.fivesorganiser.match.inviteplayers.InvitePlayersFragment
import com.gregmcgowan.fivesorganiser.match.summary.MatchSummaryFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var matchActivityViewModel: MatchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_container)

        AndroidInjection.inject(this)

        matchActivityViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MatchActivityViewModel::class.java)

        matchActivityViewModel
                .navigationEvents()
                .observeNonNull(this, this::handleNavEvent)
    }

    private fun handleNavEvent(event: MatchActivityNavigationEvent) {
        when (event) {
            ShowSummary -> showMatchSummary()
            ShowSquad -> showSquad()
            CloseScreen -> finish()
        }
    }

    private fun showSquad() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.settle,
                        R.anim.slide_down)
                .add(R.id.match_fragment_container, InvitePlayersFragment())
                .commit()
    }

    private fun showMatchSummary() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.match_fragment_container, MatchSummaryFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        matchActivityViewModel.backButtonPressed()
    }
}
