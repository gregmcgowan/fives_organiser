package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.UserDeniedPermissionUiState

val ImportContactsUiState.contacts: List<ContactItemUiState>
    get() {
        return when (this) {
            is ContactsListUiState -> this.contacts
            is ErrorUiState, LoadingUiState, ShowRequestPermissionDialogUiState,
            UserDeniedPermissionUiState, TerminalUiState -> {
                throw IllegalStateException("Attempting to get the contact list from $this UI state")
            }
        }
    }
