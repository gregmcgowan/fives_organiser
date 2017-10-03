package com.gregmcgowan.fivesorganiser.importContacts

import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ContactItemUiModel

internal fun loadingReducer(): ImportContactsUiModelReducer = { uiModel ->
    uiModel.copy(showLoading = true, showContent = false)
}

internal fun importCompleteReducer(): ImportContactsUiModelReducer = { uiModel ->
    uiModel.copy(closeScreen = true)
}

internal fun uiModelReducer(contacts: List<Contact>,
                            contactsState: Set<Int>): ImportContactsUiModelReducer = { uiModel ->
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

internal fun contactSelectedReducer(contactId: Int): ImportContactsUiModelReducer = { uiModel ->
    val mutableList = uiModel.contacts.toMutableList()
    for (itemModel in uiModel.contacts) {
        if (itemModel.contactId == contactId) {
            val index = mutableList.indexOf(itemModel)
            mutableList[index] = itemModel.copy(isSelected = true)
        }
    }
    uiModel.copy(contacts = mutableList.toList(), importContactsButtonEnabled = true)
}

internal fun contactDeselectedReducer(contactId: Int, selectedContacts : Set<Int>):
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
