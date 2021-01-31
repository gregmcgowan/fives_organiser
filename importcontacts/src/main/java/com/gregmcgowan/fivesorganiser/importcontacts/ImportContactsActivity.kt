package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.lifecycleScope
import com.gregmcgowan.fivesorganiser.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.BaseActivity
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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

        lifecycleScope.launchWhenStarted {
            importImportContactsViewModel.importContactsUiEvent.collect { event ->
                when (event) {
                    RequestPermission -> permission.requestPermission()
                    CloseScreen -> returnToPlayersScreen()
                    Idle -> {}
                }
            }
        }
        setContent {
            AppTheme {
                ImportContactsScreen(
                        importContactsUiModel = importImportContactsViewModel.uiModel,
                        eventHandler = importImportContactsViewModel::handleEvent
                )
            }
        }
    }


    override fun onPermissionGranted() {
        importImportContactsViewModel.handleEvent(ImportContactsUserEvent.ContactPermissionGrantedEvent)
    }

    override fun onPermissionDenied(userSaidNever: Boolean) {
        //TODO move to compose
        Toast.makeText(this,
                getString(R.string.permissons_denied_text),
                Toast.LENGTH_LONG).show()
    }

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}