package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.setContent
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.observeNonNull
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import dagger.hilt.android.AndroidEntryPoint

fun Context.importContactsIntent(): Intent {
    return Intent(this, ImportContactsActivity::class.java)
}

@AndroidEntryPoint
class ImportContactsActivity : BaseActivity(), PermissionResults {

    private val importImportContactsViewModel: ImportContactsViewModel by viewModels()

    private val permission = Permission(this, Manifest.permission.READ_CONTACTS)

    val hasContactPermission: Boolean
        get() = permission.hasPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.import_contacts)

        importImportContactsViewModel
                .contactUiNavLiveData
                .observeNonNull(this, this::handleNavEvents)

        importImportContactsViewModel
                .contactUiModelLiveData
                .observeNonNull(this, this::render)
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
        setContent {
            MaterialTheme {
                ImportContactsScreen(importContactsUiModel = uiModel, importImportContactsViewModel::updateContactSelectedStatus)
            }
        }
    }

    private fun closeScreen() = finish()

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        closeScreen()
    }

}