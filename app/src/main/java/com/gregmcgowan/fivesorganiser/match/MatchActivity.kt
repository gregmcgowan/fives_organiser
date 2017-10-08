package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.ui.DatePickerFragment
import com.gregmcgowan.fivesorganiser.core.ui.TimePickerFragment
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.ZonedDateTimeFormatter
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.match.MatchContract.*
import com.gregmcgowan.fivesorganiser.match.MatchContract.DatePickerUiModel.ShowDatePickerUi
import com.gregmcgowan.fivesorganiser.match.MatchContract.MatchUiEvent.*
import com.gregmcgowan.fivesorganiser.core.setTextIfNotEqual
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MatchActivity : BaseActivity(), MatchContract.Ui {

    private val timeField: TextView by find(R.id.match_time)
    private val dateField: TextView by find(R.id.match_date)
    private val locationField: EditText by find(R.id.match_location_edit_text)
    private val content: View by find(R.id.match_content)
    private val progressBar: ProgressBar by find(R.id.match_progress_bar)

    private lateinit var presenter: MatchContract.Presenter

    private lateinit var timeSelectedObservable: Observable<MatchUiEvent>
    private lateinit var dateSelectedObservable: Observable<MatchUiEvent>

    private val saveMenuItemObservable: PublishSubject<MatchUiEvent> = PublishSubject.create()
    private val backMenuItemObservable: PublishSubject<MatchUiEvent> = PublishSubject.create()

    private var dateListener: DatePickerDialog.OnDateSetListener? = null
    private var timeListener: OnTimeSetListener? = null

    private val dateObservable: Observable<MatchUiEvent> = Observable.create({ emitter ->
        dateListener =
                OnDateSetListener { _, year, month, date ->
                    emitter.onNext(DateUpdatedEvent(year, month, date))
                    emitter.setCancellable { dateListener = null }
                }
    })

    private val timeObservable: Observable<MatchUiEvent> = Observable.create { emitter ->
        timeListener =
                OnTimeSetListener { _, hour, minute ->
                    emitter.onNext(TimeUpdatedEvent(hour, minute))
                    emitter.setCancellable({ timeListener = null })
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_match)

        val toolbar = find<Toolbar>(R.id.match_toolbar).value
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        timeSelectedObservable = Observable.create { emitter ->
            timeField.setOnClickListener({
                emitter.onNext(TimeSelected())
            })
            emitter.setCancellable {
                timeField.setOnClickListener(null)
            }

        }
        dateSelectedObservable = Observable.create { emitter ->
            dateField.setOnClickListener({
                emitter.onNext(DateSelected())
            })

            emitter.setCancellable {
                dateField.setOnClickListener(null)
            }
        }

        presenter = MatchUiPresenter(
                ui = this,
                matchStore = MatchStore(
                        matchRepo = dependencies.matchRepo,
                        matchId = null
                ),
                reducers = Reducers(ZonedDateTimeFormatter())
        )
        lifecycle.addObserver(presenter)
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
                R.id.save_match -> saveMenuItemObservable.onNext(SaveButtonPressedEvent())
                R.id.home -> backMenuItemObservable.onNext(BackArrowPressedEvent())
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun render(uiModel: MatchUiModel) {
        if (!uiModel.closeScreen) {
            content.setVisible(uiModel.showContent)
            progressBar.setVisible(uiModel.loading)
            when (uiModel.timePickerUiModel) {
                is TimePickerUiModel.ShowTimePickerUi ->
                    showTimePicker(
                            uiModel.timePickerUiModel.hour,
                            uiModel.timePickerUiModel.minute,
                            true
                    )
            }
            when (uiModel.datePickerUiModel) {
                is ShowDatePickerUi ->
                    showDatePickerDialog(
                            uiModel.datePickerUiModel.year,
                            uiModel.datePickerUiModel.month,
                            uiModel.datePickerUiModel.date
                    )
            }
            timeField.setTextIfNotEqual(uiModel.time)
            dateField.setTextIfNotEqual(uiModel.date)
            locationField.setTextIfNotEqual(uiModel.location)
        } else {
            finish()
            return
        }
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int) {
        val newFragment = DatePickerFragment.newInstance(year, month, day)
        newFragment.dateListener = dateListener
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePicker(hour: Int, minute: Int, is24Hr: Boolean) {
        val newFragment = TimePickerFragment.newInstance(hour, minute, is24Hr)
        newFragment.timePickerListener = timeListener
        newFragment.show(supportFragmentManager, "timePicker")
    }

    override fun dateUpdated(): Observable<MatchUiEvent> = dateObservable

    override fun timeUpdated(): Observable<MatchUiEvent> = timeObservable

    override fun dateSelected(): Observable<MatchUiEvent> = dateSelectedObservable

    override fun timeSelected(): Observable<MatchUiEvent> = timeSelectedObservable

    override fun savePressed(): Observable<MatchUiEvent> = saveMenuItemObservable

    override fun backPressed(): Observable<MatchUiEvent> = backMenuItemObservable

    override fun locationUpdated(): Observable<MatchUiEvent> =
            RxTextView.afterTextChangeEvents(locationField)
                    .distinctUntilChanged()
                    .map { event -> LocationUpdatedEvent(event.view().text.toString()) }


}
