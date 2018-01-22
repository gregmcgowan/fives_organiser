package com.gregmcgowan.fivesorganiser.match

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import com.gregmcgowan.fivesorganiser.core.ui.DatePickerFragment
import com.gregmcgowan.fivesorganiser.core.ui.TimePickerFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

fun Context.editMatchIntent(matchId: String): Intent {
    return Intent(this, MatchActivity::class.java)
            .apply {
                putExtra(MATCH_ID_INTENT_EXTRA, matchId)
            }
}

fun Context.createMatchIntent(): Intent {
    return Intent(this, MatchActivity::class.java)
}

private const val MATCH_ID_INTENT_EXTRA = "match_id"

class MatchActivity : BaseActivity() {

    private val content: View by find(R.id.match_content)
    private val progressBar: ProgressBar by find(R.id.match_progress_bar)
    private val startTime: TextView by find(R.id.match_start_time)
    private val endTime: TextView by find(R.id.match_end_time)
    private val date: TextView by find(R.id.match_date)
    private val location: EditText by find(R.id.match_location)
    private val squadSize: EditText by find(R.id.match_squad_size)
    private val confirmedPlayers: TextView by find(R.id.match_confirmed_players_count)
    private val unknownPlayers: TextView by find(R.id.match_unknown_players_count)
    private val declinedPlayers: TextView by find(R.id.match_declined_players_count)

    private lateinit var matchViewModel: MatchViewModel

    private lateinit var dateListener: OnDateSetListener
    private lateinit var startTimeListener: OnTimeSetListener
    private lateinit var endTimeListener: OnTimeSetListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_container)

        val toolbar = find<Toolbar>(R.id.match_toolbar).value
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        matchViewModel = ViewModelProviders
                .of(this,
                        MatchUiViewModelFactory(
                                UI,
                                CommonPool,
                                intent.getStringExtra(MATCH_ID_INTENT_EXTRA),
                                dependencies.matchRepo,
                                MatchUiModelReducers(ZonedDateTimeFormatter())
                        ))
                .get(MatchViewModel::class.java)

        matchViewModel.navigationEvents().observe(this,
                Observer<MatchUiNavigationEvent?> { navEvents ->
                    navEvents?.let {
                        handleNavEvent(it)
                    }
                })
        matchViewModel.matchUiModel().observe(this,
                Observer<MatchUiModel?> { model ->
                    model?.let {
                        render(model)
                    }
                })
        startTimeListener = OnTimeSetListener { _, hour, minute -> matchViewModel.startTimeUpdated(hour, minute) }
        endTimeListener = OnTimeSetListener { _, hour, minute -> matchViewModel.endTimeUpdated(hour, minute) }
        dateListener = OnDateSetListener { _, year, month, date -> matchViewModel.dateUpdated(year, month, date) }

        startTime.setOnClickListener { matchViewModel.startTimeSelected() }
        endTime.setOnClickListener { matchViewModel.endTimeSelected() }
        date.setOnClickListener { matchViewModel.dateSelected() }

        location.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                matchViewModel.locationUpdated(location.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        squadSize.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (squadSize.text.isNotEmpty()) {
                    matchViewModel.squadSizeUpdated(squadSize.text.toString().toInt())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        matchViewModel.onViewShown()
    }

    private fun handleNavEvent(navEvent: MatchUiNavigationEvent) {
        when (navEvent) {
            is MatchUiNavigationEvent.ShowDatePicker -> showDatePickerDialog(
                    navEvent.year,
                    navEvent.month,
                    navEvent.date
            )
            is MatchUiNavigationEvent.ShowStartTimePicker -> showTimePicker(
                    navEvent.hour,
                    navEvent.minute,
                    true,
                    startTimeListener
            )
            is MatchUiNavigationEvent.ShowEndTimePicker -> showTimePicker(
                    navEvent.hour,
                    navEvent.minute,
                    true,
                    endTimeListener
            )
            MatchUiNavigationEvent.CloseScreen -> finish()
            MatchUiNavigationEvent.Idle -> {
                //Do nothing
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val toolbar = find<Toolbar>(R.id.match_toolbar).value
        toolbar.inflateMenu(R.menu.add_edit_match)
        toolbar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.save_match -> matchViewModel.saveButtonPressed()
                R.id.home -> matchViewModel.closeButtonPressed()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun render(uiModel: MatchUiModel) {
        content.setVisible(uiModel.showContent)
        progressBar.setVisible(uiModel.loading)
        startTime.setTextIfNotEqual(uiModel.startTime)
        endTime.setTextIfNotEqual(uiModel.endTime)
        date.setTextIfNotEqual(uiModel.date)
        location.setTextIfNotEqual(uiModel.location)
        squadSize.setTextIfNotEqual(uiModel.numberOfPLayers)
        confirmedPlayers.setTextIfNotEqual(uiModel.confirmedPlayersCount.toString())
        unknownPlayers.setTextIfNotEqual(uiModel.unknownPlayersCount.toString())
        declinedPlayers.setTextIfNotEqual(uiModel.declinedPlayersCount.toString())
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val newFragment = DatePickerFragment.newInstance(year, month, day)
        newFragment.dateListener = dateListener
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePicker(hour: Int, minute: Int, is24Hr: Boolean, onTimeSetListener: OnTimeSetListener) {
        val newFragment = TimePickerFragment.newInstance(hour, minute, is24Hr)
        newFragment.timePickerListener = onTimeSetListener
        newFragment.show(supportFragmentManager, "timePicker")
    }

    class MatchUiViewModelFactory(
            private val coroutineContext: CoroutineContext,
            private val backgroundContext: CoroutineContext,
            private val matchId: String?,
            private val matchRepo: MatchRepo,
            private val matchUiModelReducers: MatchUiModelReducers
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MatchViewModel(coroutineContext, backgroundContext, matchId, matchRepo, matchUiModelReducers) as T
        }
    }

}
