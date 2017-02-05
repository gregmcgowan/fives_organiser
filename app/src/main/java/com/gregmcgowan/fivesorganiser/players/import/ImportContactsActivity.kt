package com.gregmcgowan.fivesorganiser.players.import

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.authenication.FirebaseAuthentication
import com.gregmcgowan.fivesorganiser.players.PlayersFirebaseRepo

class ImportContactsActivity : AppCompatActivity(), ImportContactsContract.View {

    var importContactsPresenter: ImportContactsContract.Presenter? = null

    var contactList : RecyclerView? = null

    var progressBar : ProgressBar? = null

    val importPlayersAdapter : ImportPlayersAdapter = ImportPlayersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.import_contacts)

        contactList = findViewById(R.id.import_contacts_list) as RecyclerView
        progressBar = findViewById(R.id.import_contacts_progress_bar) as ProgressBar

        contactList?.adapter = importPlayersAdapter

        val contactImporter = AndroidContactImporter(contentResolver)
        val authentication = FirebaseAuthentication(FirebaseAuth.getInstance())

        importContactsPresenter = ImportContactsPresenter(this,
                PlayersFirebaseRepo(FirebaseDatabase.getInstance(), authentication),
                contactImporter
        )

        importContactsPresenter?.startPresenting()
    }

    override fun showContacts(contacts: List<Contact>) {
        importPlayersAdapter.setContacts(contacts)
        Log.d("GREG", "Number of contacts " + contacts.size)
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    override fun showContactList(show: Boolean) {
        if (show) {
            contactList?.visibility = View.VISIBLE
        } else {
            contactList?.visibility = View.GONE
        }
    }

    override fun showContactsError(exception: Exception) {
        Log.d("GREG", "Get contacts exception " + exception.message)
        Toast.makeText(this,"Error getting contacts", Toast.LENGTH_SHORT).show()
    }

    override fun setContactItemListener(listener: ImportContactsContract.ContactItemListener) {
        importPlayersAdapter.setListener(listener)
    }
}