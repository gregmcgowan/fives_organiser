package com.gregmcgowan.fivesorganiser.players.import

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.gregmcgowan.fivesorganiser.FivesOrganiserApp
import com.gregmcgowan.fivesorganiser.R

class ImportContactsActivity : AppCompatActivity(), ImportContactsContract.View {

    var importContactsPresenter: ImportContactsContract.Presenter? = null

    var mainContent: View? = null
    var contactList: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var addButton: Button? = null

    val importPlayersAdapter: ImportPlayersAdapter = ImportPlayersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.import_contacts)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        contactList = findViewById(R.id.import_contacts_list) as RecyclerView
        progressBar = findViewById(R.id.import_contacts_progress_bar) as ProgressBar
        mainContent = findViewById(R.id.import_contacts_main_content) as View
        addButton = findViewById(R.id.import_contacts_add_button) as Button

        contactList?.adapter = importPlayersAdapter

        val contactImporter = AndroidContactImporter(contentResolver)
        val playersRepo = getApp().dependencies.playersRepo

        importContactsPresenter = ImportContactsPresenter(this,
                playersRepo, contactImporter
        )

        importContactsPresenter?.startPresenting()

        addButton?.setOnClickListener { View -> importContactsPresenter?.handleAddButtonPressed() }
    }

    fun getApp(): FivesOrganiserApp {
        return applicationContext as FivesOrganiserApp
    }

    override fun closeScreen() {
        finish()
    }

    override fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }

    override fun setAddButtonEnabled(enabled: Boolean) {
        addButton?.isEnabled = enabled
    }

    override fun showContacts(contacts: List<Contact>) {
        importPlayersAdapter.setContacts(contacts)
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    override fun showMainContent(show: Boolean) {
        if (show) {
            mainContent?.visibility = View.VISIBLE
        } else {
            mainContent?.visibility = View.GONE
        }
    }

    override fun showContactsError(exception: Exception) {
        Log.d("GREG", "Get contacts exception " + exception.message)
        Toast.makeText(this, "Error getting contacts", Toast.LENGTH_SHORT).show()
    }

    override fun setContactItemListener(listener: ImportContactsContract.ContactItemListener) {
        importPlayersAdapter.setListener(listener)
    }
}