package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID

sealed class ImportContactsUserEvent {

    object ContactPermissionGrantedEvent : ImportContactsUserEvent()
    object AddButtonPressedEvent : ImportContactsUserEvent()

    class ContactSelectedEvent(val contactId: Long,
                               val selected: Boolean) : ImportContactsUserEvent()
}


sealed class ImportContactsUiState {

    object ShowRequestPermissionDialogUiState : ImportContactsUiState()

    object LoadingUiState : ImportContactsUiState()

    class ErrorUiState(@StringRes val errorMessage: Int = NO_STRING_RES_ID) : ImportContactsUiState()

    class ContactsListUiState(val contacts: List<ContactItemUiState>,
                              val addContactsButtonEnabled: Boolean) : ImportContactsUiState()

    object TerminalUiState : ImportContactsUiState()
}

data class ContactItemUiState(
        val name: String,
        val isSelected: Boolean,
        val contactId: Long
)

