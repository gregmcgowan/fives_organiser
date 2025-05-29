package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import javax.inject.Inject

interface ImportContactsUiStateMapper {
    fun map(
        contacts: List<Contact>,
        selectedContacts: Set<Long>,
    ): ImportContactsUiState
}

class ImportContactsUiStateMapperImpl @Inject constructor() : ImportContactsUiStateMapper {
    override fun map(
        contacts: List<Contact>,
        selectedContacts: Set<Long>,
    ): ContactsListUiState =
        contacts
            .map {
                ContactItemUiState(
                    name = it.name,
                    contactId = it.contactId,
                    isSelected = selectedContacts.contains(it.contactId),
                )
            }
            .run {
                ContactsListUiState(
                    contacts = this,
                    addContactsButtonEnabled = selectedContacts.isNotEmpty(),
                )
            }
}
