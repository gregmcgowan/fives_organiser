package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import javax.inject.Inject

class ImportContactsUiModelMapper @Inject constructor() {

    fun map(contacts: List<Contact>, selectedContacts: Set<Long>): ImportContactsUiModel {
        val hasContacts = contacts.isNotEmpty()

        return ImportContactsUiModel(
                showLoading = false,
                showContent = hasContacts,
                importContactsButtonEnabled = selectedContacts.isNotEmpty(),
                contacts = mapContactListItem(contacts, selectedContacts),
                errorMessage = getErrorMessage(hasContacts)
        )

    }

    private fun getErrorMessage(hasContacts: Boolean): Int =
            if (hasContacts) NO_STRING_RES_ID else R.string.no_contacts_message

    private fun mapContactListItem(contacts: List<Contact>, selectedContacts: Set<Long>): List<ContactItemUiModel> {
        return contacts.map {
            ContactItemUiModel(
                    name = it.name,
                    contactId = it.contactId,
                    isSelected = selectedContacts.contains(it.contactId)
            )
        }
    }

}

