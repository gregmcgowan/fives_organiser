package com.gregmcgowan.fivesorganiser.importcontacts

import javax.inject.Inject

class ImportContactsUiModelMapper @Inject constructor() {

    fun map(contacts: List<Contact>,
            selectedContacts: Set<Long>): ImportContactsUiModel {
        val contactUiModels = contacts.map {
            ContactItemUiModel(
                    name = it.name,
                    contactId = it.contactId,
                    isSelected = selectedContacts.contains(it.contactId)
            )
        }

        return ImportContactsUiModel(
                showLoading = false,
                showContent = true,
                showErrorMessage = false,
                importContactsButtonEnabled = selectedContacts.isNotEmpty(),
                contacts = contactUiModels,
                errorMessage = ""
        )
    }

}

