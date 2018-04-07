package com.gregmcgowan.fivesorganiser.match.summary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.core.ui.DatePickerFragment
import com.gregmcgowan.fivesorganiser.core.ui.TimePickerFragment
import com.gregmcgowan.fivesorganiser.match.MatchActivityViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MatchSummaryFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var matchSummaryViewModel: MatchSummaryViewModel
    private lateinit var mainActivityViewModel: MatchActivityViewModel

    private val content: View by find(R.id.match_content)
    private val progressBar: ProgressBar by find(R.id.match_progress_bar)
    private val startTime: TextView by find(R.id.match_start_time)
    private val endTime: TextView by find(R.id.match_end_time)
    private val date: TextView by find(R.id.match_date)
    private val location: EditText by find(R.id.match_location)
    private val updateSquadButton: Button by find(R.id.match_update_squad_button)
    private val confirmedPlayers: TextView by find(R.id.match_confirmed_players_count)
    private val unknownPlayers: TextView by find(R.id.match_unknown_players_count)
    private val declinedPlayers: TextView by find(R.id.match_declined_players_count)
    private val matchType: Spinner by find(R.id.match_type)

    private lateinit var dateListener: DatePickerDialog.OnDateSetListener
    private lateinit var startTimeListener: TimePickerDialog.OnTimeSetListener
    private lateinit var endTimeListener: TimePickerDialog.OnTimeSetListener
    private lateinit var matchTypeSpinnerAdapter: ArrayAdapter<String>

    var matchId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.match_summary, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.add_edit_match, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.save_match -> mainActivityViewModel.saveButtonPressed()
                R.id.home -> mainActivityViewModel.upButtonPressed()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppCompatActivity().setSupportActionBar(find<Toolbar>(R.id.match_toolbar).value)
        getAppCompatActivity().supportActionBar?.setHomeButtonEnabled(true)
        getAppCompatActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AndroidSupportInjection.inject(this)

        matchSummaryViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MatchSummaryViewModel::class.java)

        mainActivityViewModel = ViewModelProviders
                .of(requireActivity())
                .get(MatchActivityViewModel::class.java)

        matchSummaryViewModel
                .navigationEvents()
                .observeNonNull(this, this::handleNavEvent)
        matchSummaryViewModel
                .matchUiModel()
                .observeNonNull(this, this::render)

        setListeners()

        setMatchTypeAdapter()

        matchSummaryViewModel.onViewShown()
    }

    private fun setListeners() {
        startTimeListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            matchSummaryViewModel.startTimeUpdated(hour, minute)
        }
        endTimeListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            matchSummaryViewModel.endTimeUpdated(hour, minute)
        }
        dateListener = DatePickerDialog.OnDateSetListener { _, year, month, date ->
            matchSummaryViewModel.dateUpdated(year, month, date)
        }

        startTime.setOnClickListener { matchSummaryViewModel.startTimeSelected() }
        endTime.setOnClickListener { matchSummaryViewModel.endTimeSelected() }
        date.setOnClickListener { matchSummaryViewModel.dateSelected() }

        location.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                matchSummaryViewModel.locationUpdated(location.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        matchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapter: AdapterView<*>?) {
            }

            override fun onItemSelected(adapter: AdapterView<*>, view: View, position: Int, id: Long) {
                val itemAtPosition = adapter.getItemAtPosition(position) as String
                matchSummaryViewModel.matchTypeSelected(itemAtPosition)
            }
        }

        updateSquadButton.setOnClickListener {
            mainActivityViewModel.showMatchSquad()
        }
    }

    private fun setMatchTypeAdapter() {
        matchTypeSpinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mutableListOf())

        matchTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        matchType.adapter = matchTypeSpinnerAdapter
    }

    private fun render(summaryUiModel: MatchSummaryUiModel) {
        content.setVisibleOrGone(summaryUiModel.showContent)
        progressBar.setVisibleOrGone(summaryUiModel.loading)
        startTime.setTextIfNotEqual(summaryUiModel.startTime)
        endTime.setTextIfNotEqual(summaryUiModel.endTime)
        date.setTextIfNotEqual(summaryUiModel.date)
        location.setTextIfNotEqual(summaryUiModel.location)
        confirmedPlayers.setTextIfNotEqual(summaryUiModel.confirmedPlayersCount.toString())
        unknownPlayers.setTextIfNotEqual(summaryUiModel.unknownPlayersCount.toString())
        declinedPlayers.setTextIfNotEqual(summaryUiModel.declinedPlayersCount.toString())
        matchTypeSpinnerAdapter.updateIfChanged(summaryUiModel.matchTypeOptions)
        matchType.setIfNotEqual(summaryUiModel.selectedMatchTypeIndex)
    }

    private fun handleNavEvent(navEventSummary: MatchSummaryUiNavigationEvent) {
        when (navEventSummary) {
            is MatchSummaryUiNavigationEvent.ShowDatePicker -> showDatePickerDialog(
                    navEventSummary.year,
                    navEventSummary.month,
                    navEventSummary.date
            )
            is MatchSummaryUiNavigationEvent.ShowStartTimePicker -> showTimePicker(
                    navEventSummary.hour,
                    navEventSummary.minute,
                    true,
                    startTimeListener
            )
            is MatchSummaryUiNavigationEvent.ShowEndTimePicker -> showTimePicker(
                    navEventSummary.hour,
                    navEventSummary.minute,
                    true,
                    endTimeListener
            )
            MatchSummaryUiNavigationEvent.CloseScreen -> activity?.finish()
            MatchSummaryUiNavigationEvent.Idle -> {
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
            onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        val newFragment = TimePickerFragment.newInstance(hour, minute, is24Hr)
        newFragment.timePickerListener = onTimeSetListener
        newFragment.show(fragmentManager, "timePicker")
    }

}