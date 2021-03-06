package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.annotation.StringRes
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID

sealed class ImportContactsNavEvent {
    object Idle : ImportContactsNavEvent()
    object RequestPermission : ImportContactsNavEvent()
    object CloseScreen : ImportContactsNavEvent()
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
