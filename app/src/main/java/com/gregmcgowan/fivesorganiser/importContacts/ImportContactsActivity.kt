package com.gregmcgowan.fivesorganiser.importContacts

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.getApp
import com.gregmcgowan.fivesorganiser.core.setVisible
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsUiEvent
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsUiModel
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ImportContactsActivity : BaseActivity(), ImportContactsContract.Ui {

    private val mainContent: View  by find(R.id.import_contacts_main_content)
    private val contactList: RecyclerView  by find(R.id.import_contacts_list)
    private val progressBar: ProgressBar by find(R.id.import_contacts_progress_bar)
    private val addButton: Button by find(R.id.import_contacts_add_button)
    private val importPlayersAdapter: ImportPlayersAdapter = ImportPlayersAdapter()

    private lateinit var importContactsButtonEvents: Observable<ImportContactsUiEvent>

    private lateinit var importContactsPresenter: ImportContactsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.import_contacts)
        setSupportActionBar(find<Toolbar>(R.id.toolbar).value)

        contactList.adapter = importPlayersAdapter

        importContactsButtonEvents = RxView.clicks(addButton)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map { _ -> ImportContactsUiEvent.ImportContactsPressed() }

        importContactsPresenter = ImportContactsPresenter(
                importContactsUi = this,
                importContactsStore = ImportContactsStore(
                        getApp().dependencies.playersRepo,
                        AndroidContactImporter(contentResolver)
                )
        )
        lifecycle.addObserver(importContactsPresenter)
    }

    override fun contactListEvents(): Observable<ImportContactsUiEvent> {
        return importPlayersAdapter.contactSelectedObservable()
    }

    override fun importContactPressedEvents(): Observable<ImportContactsUiEvent> {
        return importContactsButtonEvents
    }

    override fun render(uiModel: ImportContactsUiModel) {
        if (!uiModel.closeScreen) {
            progressBar.setVisible(uiModel.showLoading)
            mainContent.setVisible(uiModel.showContent)
            importPlayersAdapter.setContacts(uiModel.contacts)
            addButton.isEnabled = uiModel.importContactsButtonEnabled
        } else {
            returnToPlayersScreen()
        }
    }

    private fun closeScreen() = finish()

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }


}