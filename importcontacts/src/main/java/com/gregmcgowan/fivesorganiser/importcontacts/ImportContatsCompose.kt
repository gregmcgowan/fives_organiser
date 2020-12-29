package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gregmcgowan.fivesorganiser.compose.AppTheme


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
                        showContent = true, showLoading = false, importContactsButtonEnabled = true),
                { _, _ -> }, {}
        )
    }

}

@Composable
fun ImportContactsScreen(importContactsUiModel: ImportContactsUiModel,
                         onContactChanged: (Long, Boolean) -> Unit,
                         onAddButton: () -> Unit) {
    Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(id = R.string.import_contacts_title)) }) },
            bodyContent = {
                Column {
                    ContactList(importContactsUiModel.contacts, onContactChanged)
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Button(
                                content = { Text(stringResource(id = R.string.add)) },
                                onClick = onAddButton,
                                enabled = importContactsUiModel.importContactsButtonEnabled
                        )
                    }
                }
            }
    )

}

@Composable
fun ContactList(contacts: List<ContactItemUiModel>,
                onContactChanged: (Long, Boolean) -> Unit) {
    LazyColumnFor(items = contacts) { contact ->
        ContactItem(contact = contact, onContactChanged)
    }
}


@Composable
fun ContactItem(contact: ContactItemUiModel,
                onContactChanged: (Long, Boolean) -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Checkbox(
                        checked = contact.isSelected,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        onCheckedChange = { selected -> onContactChanged(contact.contactId, selected) }
                )
                Text(contact.name, modifier = Modifier.fillMaxWidth())
            }
    )
}


///
// setSupportActionBar(findViewById(R.id.import_contacts_toolbar))


//findViewById<RecyclerView>(R.id.import_contacts_list).adapter = importPlayersAdapter
//        findViewById<View>(R.id.import_contacts_add_button)
//                .setOnClickListener { importImportContactsViewModel.onAddButtonPressed() }
//
//        findViewById<View>(R.id.import_contacts_progress_bar).setVisibleOrGone(uiModel.showLoading)
//        findViewById<View>(R.id.import_contacts_main_content).setVisibleOrGone(uiModel.showContent)
//        findViewById<View>(R.id.import_contacts_error_message).setVisibleOrGone(uiModel.errorMessage != NO_STRING_RES_ID)
//        findViewById<TextView>(R.id.import_contacts_error_message).setTextIfValidRes(uiModel.errorMessage)
//        findViewById<View>(R.id.import_contacts_add_button).isEnabled = uiModel.importContactsButtonEnabled
//        importPlayersAdapter.setContacts(uiModel.contacts)