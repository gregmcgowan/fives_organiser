package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID

sealed class ImportContactsUserEvent {

    object ContactPermissionGrantedEvent : ImportContactsUserEvent()
    object AddButtonPressedEvent : ImportContactsUserEvent()

    class ContactSelectedEvent(val contactId: Long,
                               val selected: Boolean) : ImportContactsUserEvent()
}

sealed class ImportContactsUiEvent {
    object RequestPermission : ImportContactsUiEvent()
    object CloseScreen : ImportContactsUiEvent()
}

data class ImportContactsUiModel(
        val contacts: List<ContactItemUiModel>,
        val showLoading: Boolean,
        val showContent: Boolean,
        val importContactsButtonEnabled: Boolean,
        @StringRes val errorMessage: Int = NO_STRING_RES_ID
)

data class ContactItemUiModel(
        val name: String,
        val isSelected: Boolean,
        val contactId: Long
)
