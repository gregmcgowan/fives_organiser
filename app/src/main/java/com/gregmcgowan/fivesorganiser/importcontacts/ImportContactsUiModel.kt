package com.gregmcgowan.fivesorganiser.importcontacts

typealias ImportContactsUiModelReducer = (ImportContactsUiModel) -> ImportContactsUiModel

sealed class ImportContactsNavEvent {
    object Idle : ImportContactsNavEvent()
    object CloseScreen : ImportContactsNavEvent()
}

data class ImportContactsUiModel(
        val contacts: List<ContactItemUiModel>,
        val showLoading: Boolean,
        val showContent: Boolean,
        val importContactsButtonEnabled: Boolean,
        val showErrorMessage: Boolean,
        val errorMessage: String?
)

data class ContactItemUiModel(
        val name: String,
        val isSelected: Boolean,
        val contactId: Long
)