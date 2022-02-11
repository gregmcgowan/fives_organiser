package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionGrantedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent


@Composable
fun ImportContactsScreen(returnToPlayersScreen: () -> Unit) {
    val importContactsViewModel: ImportContactsViewModel = hiltViewModel()
    ImportContactsScreen(
            importContactsViewModel.uiState,
            userEventHandler = { event -> importContactsViewModel.handleEvent(event) },
            returnToPlayersScreen = returnToPlayersScreen
    )
}


@Composable
private fun ImportContactsScreen(importContactsUiState: ImportContactsUiState,
                                 userEventHandler: (ImportContactsUserEvent) -> Unit,
                                 returnToPlayersScreen: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { result ->
        if (result) {
            userEventHandler.invoke(ContactPermissionGrantedEvent)
        } else {
            TODO("show some error UI")
        }
    }
    when (importContactsUiState) {
        is LoadingUiState -> Loading()
        is ShowRequestPermissionDialogUiState -> {
            SideEffect { launcher.launch(Manifest.permission.READ_CONTACTS) }
        }
        is ContactsListUiState -> {
            Column {
                Row(modifier = Modifier.weight(1.0f)) {
                    LazyColumn {
                        items(importContactsUiState.contacts) { contact ->
                            ContactItem(contact, userEventHandler)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)) {
                    Button(
                            content = { Text(stringResource(id = R.string.import_contacts_add)) },
                            onClick = { userEventHandler.invoke(AddButtonPressedEvent) },
                            enabled = importContactsUiState.addContactsButtonEnabled
                    )
                }
            }
        }
        is ErrorUiState -> {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)) {
                Text(stringResource(id = importContactsUiState.errorMessage))
            }
        }
        TerminalUiState -> {
            returnToPlayersScreen.invoke()
        }
    }
}


@Composable
fun ContactItem(contact: ContactItemUiState,
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
                        onCheckedChange = { selected ->
                            eventHandler.invoke(ContactSelectedEvent(contact.contactId, selected))
                        }
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
                ContactsListUiState(
                        listOf(
                                ContactItemUiState(name = "Greg", isSelected = true, contactId = 1),
                                ContactItemUiState(name = "Frances", isSelected = true, contactId = 2),
                                ContactItemUiState(name = "Joe Wicks", isSelected = true, contactId = 3)
                        ),
                        addContactsButtonEnabled = true,
                ),
                { }, { }
        )
    }
}
