package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.permissions.PermissionResults
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

fun Context.importContactsIntent(): Intent {
    return Intent(this, ImportContactsActivity::class.java)
}

private const val PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class ImportContactsActivity : AppCompatActivity(), PermissionResults {

    private val importImportContactsViewModel: ImportContactsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            importImportContactsViewModel.importContactsUiEvent.collect { event ->
                when (event) {
                    RequestPermission -> requestPermissionForContacts()
                    CloseScreen -> returnToPlayersScreen()
                    Idle -> {
                    }
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

    private fun requestPermissionForContacts() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CODE)
    }


    override fun onPermissionGranted() {
        importImportContactsViewModel.handleEvent(ImportContactsUserEvent.ContactPermissionGrantedEvent)
    }

    override fun onPermissionDenied(userSaidNever: Boolean) {
        //TODO move to compose
        Toast.makeText(this,
                getString(R.string.permissions_denied_text),
                Toast.LENGTH_LONG).show()
    }

    private fun returnToPlayersScreen() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}