package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent

@Composable
fun ImportContactsScreen(importContactsUiModel: ImportContactsUiModel,
                         eventHandler: (ImportContactsUserEvent) -> Unit) {
    Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(id = R.string.import_contacts_title)) }) },
            content = { ImportContactsBodyContent(importContactsUiModel, eventHandler) }
    )
}

@Composable
private fun ImportContactsBodyContent(importContactsUiModel: ImportContactsUiModel,
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
                            content = { Text(stringResource(id = R.string.add)) },
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
        ) { _ -> }
    }
}