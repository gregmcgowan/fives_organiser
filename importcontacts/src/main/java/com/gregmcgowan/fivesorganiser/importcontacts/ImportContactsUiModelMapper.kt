package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import javax.inject.Inject

class ImportContactsUiModelMapper @Inject constructor() {

    fun map(contacts: List<Contact>, selectedContacts: Set<Long>) =
            ImportContactsUiModel(
                    showLoading = false,
                    showContent = contacts.isNotEmpty(),
                    importContactsButtonEnabled = selectedContacts.isNotEmpty(),
                    contacts = mapContacts(contacts, selectedContacts),
                    errorMessage = getErrorMessage(contacts.isNotEmpty())
            )

    private fun getErrorMessage(hasContacts: Boolean): Int =
            if (hasContacts) NO_STRING_RES_ID else R.string.no_contacts_message

    private fun mapContacts(contacts: List<Contact>,
                            selectedContacts: Set<Long>): List<ContactItemUiModel> =
            contacts.map {
                ContactItemUiModel(
                        name = it.name,
                        contactId = it.contactId,
                        isSelected = selectedContacts.contains(it.contactId)
                )
            }

}

