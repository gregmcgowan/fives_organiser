package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent

@Composable
fun ImportContactsScreen() {
    val importContactsViewModel: ImportContactsViewModel = hiltViewModel()
//    lifecycleScope.launchWhenStarted {
//        importImportContactsViewModel.importContactsUiEvent.collect { event ->
//            when (event) {
//                ImportContactsUiEvent.RequestPermission -> requestPermissionForContacts()
//                ImportContactsUiEvent.CloseScreen -> returnToPlayersScreen()
//                ImportContactsUiEvent.Idle -> {
//                }
//            }
//        }
//    }



    ImportContactsScreen(importContactsViewModel.uiModel) { _ -> }
}
//
//private fun requestPermissionForContacts() {
//    ActivityCompat.requestPermissions(this,
//            arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CODE)
//}
//
//override fun onPermissionGranted() {
//    importImportContactsViewModel.handleEvent(ImportContactsUserEvent.ContactPermissionGrantedEvent)
//}
//
//override fun onPermissionDenied(userSaidNever: Boolean) {
//    //TODO move to compose
//    Toast.makeText(this,
//            getString(R.string.permissions_denied_text),
//            Toast.LENGTH_LONG).show()
//}
//
//private fun returnToPlayersScreen() {
//    setResult(Activity.RESULT_OK)
//    finish()
//}

@Composable
private fun ImportContactsScreen(importContactsUiModel: ImportContactsUiModel,
                                 eventHandler: (ImportContactsUserEvent) -> Unit) {
    when {
        importContactsUiModel.showContent -> {
            Column {
                Row(modifier = Modifier.weight(1.0f)) {
                    ContactList(importContactsUiModel.contacts, eventHandler)
                }
                Row(horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)) {
                    Button(
                            content = { Text(stringResource(id = R.string.import_contacts_add)) },
                            onClick = { eventHandler.invoke(ImportContactsUserEvent.AddButtonPressedEvent) },
                            enabled = importContactsUiModel.importContactsButtonEnabled
                    )
                }
            }
        }
        importContactsUiModel.showLoading -> Loading()
        importContactsUiModel.errorMessage != NO_STRING_RES_ID -> {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)) {
                Text(stringResource(id = importContactsUiModel.errorMessage))
            }
        }
    }
}


@Composable
fun ContactList(contacts: List<ContactItemUiModel>,
                eventHandler: (ImportContactsUserEvent) -> Unit) {
    LazyColumn {
        items(contacts) { contact ->
            ContactItem(contact, eventHandler)
        }
    }
}


@Composable
fun ContactItem(contact: ContactItemUiModel,
                eventHandler: (ImportContactsUserEvent) -> Unit) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Checkbox(
                        checked = contact.isSelected,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        onCheckedChange = { selected -> eventHandler.invoke(ContactSelectedEvent(contact.contactId, selected)) }
                )
                Text(contact.name, modifier = Modifier.fillMaxWidth())
            }
    )
}

@Preview
@Composable
fun Preview() {
    AppTheme {
        ImportContactsScreen(
                ImportContactsUiModel(
                        listOf(
                                ContactItemUiModel(name = "Greg", isSelected = true, contactId = 1),
                                ContactItemUiModel(name = "Frances", isSelected = true, contactId = 2),
                                ContactItemUiModel(name = "Joe Wicks", isSelected = true, contactId = 3)
                        ),
                        showContent = true,
                        showLoading = false,
                        importContactsButtonEnabled = true,
                        errorMessage = R.string.generic_error_message
                ),
        ) { }
    }
}