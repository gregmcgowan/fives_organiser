package com.gregmcgowan.fivesorganiser.match.details

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.core.ui.DatePickerFragment
import com.gregmcgowan.fivesorganiser.core.ui.EditTextDebounce
import com.gregmcgowan.fivesorganiser.core.ui.TimePickerFragment
import com.gregmcgowan.fivesorganiser.match.MATCH_ID_INTENT_EXTRA
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import com.gregmcgowan.fivesorganiser.match.MatchFragment
import com.gregmcgowan.fivesorganiser.match.R
import com.gregmcgowan.fivesorganiser.match.details.MatchDetailsNavEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.match_details.*
import timber.log.Timber

@AndroidEntryPoint
class MatchDetailsFragment : MatchFragment, BaseFragment() {

    companion object {
        fun newInstance(matchId: String): MatchDetailsFragment =
                MatchDetailsFragment().apply {
                    val args = Bundle()
                    args.putString(MATCH_ID_INTENT_EXTRA, matchId)
                    arguments = args
                }
    }

    private val matchDateTimeLocationViewModel: MatchDetailsViewModel by viewModels()
    private val matchActivityViewModel: MatchActivityViewModel by activityViewModels()

    private lateinit var matchTypeSpinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.match_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppCompatActivity().setSupportActionBar(match_date_time_location_toolbar)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setMatchTypeAdapter()

        setListeners()

        matchDateTimeLocationViewModel.uiModelLiveData
                .observeNonNull(this, this::render)

        matchDateTimeLocationViewModel.navLiveData
                .observeNonNull(this, this::handleNavEvent)

        matchDateTimeLocationViewModel.start(arguments?.getString(MATCH_ID_INTENT_EXTRA))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                matchDateTimeLocationViewModel.backPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setListeners() {
        match_start_time.setOnClickListener { matchDateTimeLocationViewModel.startTimeSelected() }
        match_end_time.setOnClickListener { matchDateTimeLocationViewModel.endTimeSelected() }
        match_date.setOnClickListener { matchDateTimeLocationViewModel.dateSelected() }

        val editTextDebounce = EditTextDebounce(match_location)
        editTextDebounce.watch { s -> matchDateTimeLocationViewModel.locationUpdated(s) }
        match_details_next_button.setOnClickListener { matchDateTimeLocationViewModel.nextButtonPressed() }

        match_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapter: AdapterView<*>?) {
            }

            override fun onItemSelected(adapter: AdapterView<*>, view: View, position: Int, id: Long) {
                val itemAtPosition = adapter.getItemAtPosition(position) as String
                matchDateTimeLocationViewModel.matchTypeSelected(itemAtPosition)
            }
        }
    }

    private fun setMatchTypeAdapter() {
        matchTypeSpinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mutableListOf())

        matchTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        match_type_spinner.adapter = matchTypeSpinnerAdapter
    }


    private fun render(uiModel: MatchDetailsUiModel) {
        Timber.d("Rendering $uiModel")

        match_date_time_location_content_group.setVisibleOrGone(uiModel.showContent)
        match_details_progress_bar.setVisibleOrGone(uiModel.loading)
        match_start_time.setTextIfNotEqual(uiModel.startTime)
        match_end_time.setTextIfNotEqual(uiModel.endTime)
        match_date.setTextIfNotEqual(uiModel.date)
        match_location.setTextIfNotEqual(uiModel.location)
        matchTypeSpinnerAdapter.updateIfChanged(uiModel.matchTypeOptions)
        match_type_spinner.setIfNotEqual(uiModel.selectedMatchTypeIndex)
        match_details_next_button.setVisibleOrGone(uiModel.showCreateSquadButton)
    }

    private fun handleNavEvent(navEvent: MatchDetailsNavEvent) {
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
                        OnTimeSetListener { _, hour, minute ->
                            matchDateTimeLocationViewModel.startTimeUpdated(hour, minute)
                        }
                )
            }
            is ShowEndTimePicker -> {
                showTimePicker(
                        navEvent.hour,
                        navEvent.minute,
                        true,
                        OnTimeSetListener { _, hour, minute ->
                            matchDateTimeLocationViewModel.endTimeUpdated(hour, minute)
                        }
                )
            }
            is GoToSquadScreenButtonPressed -> {
                matchActivityViewModel.nextInNewMatchFlow(navEvent.matchId)
            }
            is BackPressed -> {
                matchActivityViewModel.backButtonPressed()
            }
            is MatchDetailsNavEvent.Idle -> {
                //Do nothing
            }
        }
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val newFragment = DatePickerFragment.newInstance(year, month, day)
        newFragment.dateListener = OnDateSetListener { _, updatedYear, updatedMonth, date ->
            matchDateTimeLocationViewModel.dateUpdated(updatedYear, updatedMonth, date)
        }
        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun showTimePicker(
            hour: Int,
            minute: Int,
            is24Hr: Boolean,
            onTimeSetListener: OnTimeSetListener) {
        val newFragment = TimePickerFragment.newInstance(hour, minute, is24Hr)
        newFragment.timePickerListener = onTimeSetListener
        newFragment.show(parentFragmentManager, "timePicker")
    }

    override fun consumeBackPress(): Boolean {
        matchDateTimeLocationViewModel.backPressed()
        return true
    }
}