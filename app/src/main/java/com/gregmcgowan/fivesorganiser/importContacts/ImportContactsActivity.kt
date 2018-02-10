package com.gregmcgowan.fivesorganiser.importContacts

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import com.gregmcgowan.fivesorganiser.core.setVisible
import javax.inject.Inject

class ImportContactsActivity : BaseActivity(), PermissionResults {

    private val mainContent: View  by find(R.id.import_contacts_main_content)
    private val contactList: RecyclerView  by find(R.id.import_contacts_list)
    private val progressBar: ProgressBar by find(R.id.import_contacts_progress_bar)
    private val addButton: Button by find(R.id.import_contacts_add_button)
    private val importPlayersAdapter: ImportPlayersAdapter = ImportPlayersAdapter()

    private lateinit var importImportContactsViewModel: ImportContactsViewModel

    @Inject lateinit var viewHolderFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.import_contacts)
        setSupportActionBar(find<Toolbar>(R.id.toolbar).value)

        contactList.adapter = importPlayersAdapter
        importPlayersAdapter.contactListInteractions = object : ImportPlayersAdapter.ContactListInteraction {
            override fun contactSelected(contactId: Long) {
                importImportContactsViewModel.contactSelected(contactId)
            }

            override fun contactDeselected(contactId: Long) {
                importImportContactsViewModel.contactDeselected(contactId)
            }
        }

        DaggerImportContactsComponent
                .builder()
                .appComponent(appComponent)
                .contentResolver(contentResolver)
                .build()
                .inject(this)

        importImportContactsViewModel = ViewModelProviders
                .of(this, viewHolderFactory)
                .get(ImportContactsViewModel::class.java)

        importImportContactsViewModel
                .navEvents()
                .observeNonNull(this, this::handleNavEvents)

        importImportContactsViewModel
                .uiModel()
                .observeNonNull(this, this::render)

        addButton.setOnClickListener { _ -> importImportContactsViewModel.onAddButtonPressed() }

        val permission = Permission(this, Manifest.permission.READ_CONTACTS)
        if (permission.hasPermission()) {
            onPermissionGranted()
        } else {
            permission.requestPermission()
        }
    }

    private fun handleNavEvents(navEvent: ImportContactsNavEvent) {
        when (navEvent) {
            ImportContactsNavEvent.Idle -> {
                //Nothing
            }
            ImportContactsNavEvent.CloseScreen -> {
                returnToPlayersScreen()
            }
        }
    }

    override fun onPermissionGranted() {
        importImportContactsViewModel.onViewShown()
    }

    override fun onPermissionDenied(userSaidNever: Boolean) {
        Toast.makeText(this, "We need contact permission to import from your contacts",
                Toast.LENGTH_LONG).show()
    }

    private fun render(uiModel: ImportContactsUiModel) {
        progressBar.setVisible(uiModel.showLoading)
        mainContent.setVisible(uiModel.showContent)
        importPlayersAdapter.setContacts(uiModel.contacts)
        addButton.isEnabled = uiModel.importContactsButtonEnabled
    }

    private fun closeScreen() = finish()

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }


}