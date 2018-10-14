package com.gregmcgowan.fivesorganiser.importcontacts

import javax.inject.Inject

class ImportContactsUiModelMapper @Inject constructor() {

    fun map(contacts: List<Contact>,
            selectedContacts: Set<Long>) =
            ImportContactsUiModel(
                    showLoading = false,
                    showContent = true,
                    showErrorMessage = false,
                    importContactsButtonEnabled = selectedContacts.isNotEmpty(),
                    contacts = contacts.map {
                        ContactItemUiModel(
                                name = it.name,
                                contactId = it.contactId,
                                isSelected = selectedContacts.contains(it.contactId)
                        )
                    },
                    errorMessage = ""
            )

}

