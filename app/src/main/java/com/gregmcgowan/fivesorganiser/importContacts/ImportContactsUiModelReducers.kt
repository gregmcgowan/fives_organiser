package com.gregmcgowan.fivesorganiser.importContacts


internal fun loadingUiModel(): ImportContactsUiModelReducer = { uiModel ->
    uiModel.copy(showLoading = true, showContent = false)
}

internal fun contactListUiModel(contacts: List<Contact>,
                                contactsState: Set<Long>): ImportContactsUiModelReducer = { uiModel ->
    val contactUiModels = contacts.map {
        ContactItemUiModel(
                name = it.name,
                contactId = it.contactId,
                isSelected = contactsState.contains(it.contactId)
        )
    }

    uiModel.copy(
            showLoading = false,
            showContent = true,
            showErrorMessage = false,
            importContactsButtonEnabled = false,
            contacts = contactUiModels
    )
}

internal fun contactSelectedUiModel(contactId: Long): ImportContactsUiModelReducer = { uiModel ->
    val mutableList = uiModel.contacts.toMutableList()
    for (itemModel in uiModel.contacts) {
        if (itemModel.contactId == contactId) {
            val index = mutableList.indexOf(itemModel)
            mutableList[index] = itemModel.copy(isSelected = true)
        }
    }
    uiModel.copy(contacts = mutableList.toList(), importContactsButtonEnabled = true)
}

internal fun contactDeselectedUiModel(contactId: Long, selectedContacts : Set<Long>):
        ImportContactsUiModelReducer = { uiModel ->
    val mutableList = uiModel.contacts.toMutableList()
    for (itemModel in uiModel.contacts) {
        if (itemModel.contactId == contactId) {
            val index = mutableList.indexOf(itemModel)
            mutableList[index] = itemModel.copy(isSelected = false)
        }
    }
    uiModel.copy(contacts = mutableList.toList(),
            importContactsButtonEnabled = selectedContacts.isNotEmpty())
}
