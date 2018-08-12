package com.gregmcgowan.fivesorganiser.match.timelocation

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.core.ui.DatePickerFragment
import com.gregmcgowan.fivesorganiser.core.ui.EditTextDebounce
import com.gregmcgowan.fivesorganiser.core.ui.TimePickerFragment
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchFragment
import com.gregmcgowan.fivesorganiser.match.timelocation.MatchTimeAndLocationNavEvent.*
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

class MatchTimeAndLocationFragment : MatchFragment, BaseFragment() {

    companion object {
        fun newInstance(matchId: String): MatchTimeAndLocationFragment =
                MatchTimeAndLocationFragment().apply {
                    val args = Bundle()
                    args.putString(MATCH_ID_INTENT_EXTRA, matchId)
                    arguments = args
                }
    }

    var matchId: String? = null
        get() =  arguments?.getString(MATCH_ID_INTENT_EXTRA)

    private val content: View by find(R.id.match_date_time_location_content_group)
    private val progressBar: ProgressBar by find(R.id.match_date_time_location_progress_bar)
    private val startTime: TextView by find(R.id.match_start_time)
    private val endTime: TextView by find(R.id.match_end_time)
    private val date: TextView by find(R.id.match_date)
    private val location: EditText by find(R.id.match_location)
    private val nextButton: Button by find(R.id.match_time_location_next_button)

    private lateinit var dateListener: OnDateSetListener
    private lateinit var startTimeListener: OnTimeSetListener
    private lateinit var endTimeListener: OnTimeSetListener

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var matchDateTimeLocationViewModel: MatchTimeAndLocationViewModel
    private lateinit var matchActivityViewModel: MatchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.match_time_and_location, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AndroidSupportInjection.inject(this)

        getAppCompatActivity().setSupportActionBar(find<Toolbar>(R.id.match_date_time_location_toolbar).value)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        matchDateTimeLocationViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MatchTimeAndLocationViewModel::class.java)

        setListeners()

        matchActivityViewModel = ViewModelProviders
                .of(requireActivity())
                .get(MatchActivityViewModel::class.java)

        matchDateTimeLocationViewModel.uiModelLiveData
                .observeNonNull(this, this::render)

        matchDateTimeLocationViewModel.navLiveData
                .observeNonNull(this, this::handleNavEvent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.let {
            when (item.itemId) {
                android.R.id.home -> {
                    matchDateTimeLocationViewModel.backPressed()
                    true
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        } ?: return super.onOptionsItemSelected(item)

    }

    private fun setListeners() {
        startTimeListener = OnTimeSetListener { _, hour, minute ->
            matchDateTimeLocationViewModel.startTimeUpdated(hour, minute)
        }
        endTimeListener = OnTimeSetListener { _, hour, minute ->
            matchDateTimeLocationViewModel.endTimeUpdated(hour, minute)
        }
        dateListener = OnDateSetListener { _, year, month, date ->
            matchDateTimeLocationViewModel.dateUpdated(year, month, date)
        }

        startTime.setOnClickListener { matchDateTimeLocationViewModel.startTimeSelected() }
        endTime.setOnClickListener { matchDateTimeLocationViewModel.endTimeSelected() }
        date.setOnClickListener { matchDateTimeLocationViewModel.dateSelected() }

        val editTextDebounce = EditTextDebounce(location)
        editTextDebounce.watch { s -> matchDateTimeLocationViewModel.locationUpdated(s) }
        nextButton.setOnClickListener { matchDateTimeLocationViewModel.nextButtonPressed() }

    }

    private fun render(uiModel: MatchTimeAndLocationUiModel) {
        Timber.d("Rendering $uiModel")

        content.setVisibleOrGone(uiModel.showContent)
        progressBar.setVisibleOrGone(uiModel.loading)
        startTime.setTextIfNotEqual(uiModel.startTime)
        endTime.setTextIfNotEqual(uiModel.endTime)
        date.setTextIfNotEqual(uiModel.date)
        location.setTextIfNotEqual(uiModel.location)
        nextButton.setVisibleOrGone(uiModel.showCreateSquadButton)
    }

    private fun handleNavEvent(navEvent: MatchTimeAndLocationNavEvent) {
        when (navEvent) {
            is ShowDatePicker -> {
                showDatePickerDialog(
                        navEvent.year,
                        navEvent.month,
                        navEvent.date
                )
            }
            is ShowStartTimePicker -> {
                showTimePicker(
                        navEvent.hour,
                        navEvent.minute,
                        true,
                        startTimeListener
                )
            }
            is ShowEndTimePicker -> {
                showTimePicker(
                        navEvent.hour,
                        navEvent.minute,
                        true,
                        endTimeListener
                )
            }
            is GoToSquadScreenButtonPressed -> {
                matchActivityViewModel.nextInNewMatchFlow(navEvent.matchId)
            }
            is BackPressed -> {
                matchActivityViewModel.backButtonPressed()
            }
            is MatchTimeAndLocationNavEvent.Idle -> {
                //Do nothing
            }
        }
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val newFragment = DatePickerFragment.newInstance(year, month, day)
        newFragment.dateListener = dateListener
        newFragment.show(fragmentManager, "datePicker")
    }

    private fun showTimePicker(
            hour: Int,
            minute: Int,
            is24Hr: Boolean,
            onTimeSetListener: OnTimeSetListener) {
        val newFragment = TimePickerFragment.newInstance(hour, minute, is24Hr)
        newFragment.timePickerListener = onTimeSetListener
        newFragment.show(fragmentManager, "timePicker")
    }

    override fun consumeBackPress(): Boolean {
        matchDateTimeLocationViewModel.backPressed()
        return true
    }
}