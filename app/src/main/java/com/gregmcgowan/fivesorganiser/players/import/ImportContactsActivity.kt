package com.gregmcgowan.fivesorganiser.players.import

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ViewBinder
import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.find
import com.gregmcgowan.fivesorganiser.getApp

class ImportContactsActivity : AppCompatActivity(), ImportContactsContract.View {

    lateinit var importContactsPresenter: ImportContactsContract.Presenter

    val mainContent: View  by find<View>(R.id.import_contacts_main_content)
    val contactList: RecyclerView  by find<RecyclerView>(R.id.import_contacts_list)
    val progressBar: ProgressBar by find<ProgressBar>(R.id.import_contacts_progress_bar)
    val addButton: Button by find<Button>(R.id.import_contacts_add_button)

    val importPlayersAdapter: ImportPlayersAdapter = ImportPlayersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.import_contacts)
        setSupportActionBar(find<Toolbar>(R.id.toolbar).value)

        addButton.setOnClickListener { importContactsPresenter.handleAddButtonPressed() }
        contactList.adapter = importPlayersAdapter

        importContactsPresenter = ImportContactsPresenter(
                this,
                getApp().dependencies.playersRepo,
                AndroidContactImporter(contentResolver)
        )
        importContactsPresenter.startPresenting()

    }
    override var viewState: ViewState by ViewBinder {
        when (it) {
            is ViewState.Loading -> {
                showLoading()
            }
            is ViewState.Error -> {
                showError(it.errorMessage ?: "Error")
            }
            is ViewState.Success<*> -> {
                showSuccess(it)
            }
        }
    }

    private fun  showSuccess(it: ViewState.Success<*>) {
        if (it.item is ImportContactsContract.ImportContactsModel) {
            showContacts(it.item.contacts)
            showProgress(false)
            showMainContent(true)
        }
    }

    private fun showError(message: String) {
        showProgress(false)
        showMainContent(false)
        showContactsError(message)
    }

    private fun showLoading() {
        showProgress(true)
        showMainContent(false)
    }

    override fun closeScreen() = finish()

    override fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }

    override fun setAddButtonEnabled(enabled: Boolean) {
        addButton.isEnabled = enabled
    }

    fun showContacts(contacts: List<Contact>) = importPlayersAdapter.setContacts(contacts)

    fun showProgress(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    fun showMainContent(show: Boolean) {
        if (show) {
            mainContent.visibility = View.VISIBLE
        } else {
            mainContent.visibility = View.GONE
        }
    }

    fun showContactsError(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun setContactItemListener(listener: ImportContactsContract.ContactItemListener) {
        importPlayersAdapter.setListener(listener)
    }
}