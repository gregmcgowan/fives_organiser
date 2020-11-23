package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.gregmcgowan.fivesorganiser.core.*
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.import_contacts.*

fun Context.importContactsIntent(): Intent {
    return Intent(this, ImportContactsActivity::class.java)
}

@AndroidEntryPoint
class ImportContactsActivity : BaseActivity(), PermissionResults {

    private val importImportContactsViewModel: ImportContactsViewModel by viewModels()

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
        importImportContactsViewModel.onContactsPermissionGranted()
    }

    override fun onPermissionDenied(userSaidNever: Boolean) {
        Toast.makeText(this,
                getString(R.string.permissons_denied_text),
                Toast.LENGTH_LONG).show()
    }

    private fun render(uiModel: ImportContactsUiModel) {
        import_contacts_progress_bar.setVisibleOrGone(uiModel.showLoading)
        import_contacts_main_content.setVisibleOrGone(uiModel.showContent)
        import_contacts_error_message.setVisibleOrGone(uiModel.errorMessage != NO_STRING_RES_ID)
        import_contacts_error_message.setTextIfValidRes(uiModel.errorMessage)
        import_contacts_add_button.isEnabled = uiModel.importContactsButtonEnabled
        importPlayersAdapter.setContacts(uiModel.contacts)
    }

    private fun closeScreen() = finish()

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }

}