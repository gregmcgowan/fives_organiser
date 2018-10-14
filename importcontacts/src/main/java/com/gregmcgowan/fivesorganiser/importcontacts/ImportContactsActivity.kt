package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import com.gregmcgowan.fivesorganiser.core.setVisibleOrGone
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.import_contacts.*
import javax.inject.Inject

fun Context.importContactsIntent(): Intent {
    return Intent(this, ImportContactsActivity::class.java)
}

class ImportContactsActivity : BaseActivity(), PermissionResults {

    @Inject
    lateinit var viewHolderFactory: ViewModelProvider.Factory
    private lateinit var importImportContactsViewModel: ImportContactsViewModel

    private val importPlayersAdapter: ImportPlayersAdapter = ImportPlayersAdapter()

    private val permission = Permission(this, Manifest.permission.READ_CONTACTS)

    val hasContactPermission: Boolean
        get() = permission.hasPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.import_contacts)
        setSupportActionBar(import_contacts_toolbar)

        import_contacts_list.adapter = importPlayersAdapter

        importPlayersAdapter.contactListInteractions = object : ImportPlayersAdapter.ContactListInteraction {
            override fun contactSelected(contactId: Long) {
                importImportContactsViewModel.contactSelected(contactId)
            }

            override fun contactDeselected(contactId: Long) {
                importImportContactsViewModel.contactDeselected(contactId)
            }
        }

        AndroidInjection.inject(this)

        importImportContactsViewModel = ViewModelProviders
                .of(this, viewHolderFactory)
                .get(ImportContactsViewModel::class.java)

        importImportContactsViewModel
                .contactUiNavLiveData
                .observeNonNull(this, this::handleNavEvents)

        importImportContactsViewModel
                .contactUiModelLiveData
                .observeNonNull(this, this::render)

        import_contacts_add_button.setOnClickListener { importImportContactsViewModel.onAddButtonPressed() }
    }

    private fun handleNavEvents(navEvent: ImportContactsNavEvent) {
        when (navEvent) {
            ImportContactsNavEvent.Idle -> {
                //Nothing
            }
            ImportContactsNavEvent.CloseScreen -> {
                returnToPlayersScreen()
            }
            ImportContactsNavEvent.RequestPermission -> {
                permission.requestPermission()
            }
        }
    }

    override fun onPermissionGranted() {
        importImportContactsViewModel.loadContacts()
    }

    override fun onPermissionDenied(userSaidNever: Boolean) {
        Toast.makeText(this,
                getString(R.string.permissons_denied_text),
                Toast.LENGTH_LONG).show()
    }

    private fun render(uiModel: ImportContactsUiModel) {
        import_contacts_progress_bar.setVisibleOrGone(uiModel.showLoading)
        import_contacts_main_content.setVisibleOrGone(uiModel.showContent)
        import_contacts_add_button.isEnabled = uiModel.importContactsButtonEnabled
        importPlayersAdapter.setContacts(uiModel.contacts)
    }

    private fun closeScreen() = finish()

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }


}