package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionGrantedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class ImportContactsViewModel @Inject constructor(
        private val uiStateMapper: ImportContactsUiStateMapper,
        private val savePlayersUseCase: SavePlayersUseCase,
        private val getContactsUseCase: GetContactsUseCase,
        contactsPermission: Permission
) : ViewModel() {

    var uiState: ImportContactsUiState by mutableStateOf(value = LoadingUiState)
        private set

    private val selectedContacts: Set<Long>
        get() {
            return uiState.safeContacts
                    .filter { it.isSelected }
                    .map { it.contactId }
                    .toSet()

        }

    init {
        if (contactsPermission.hasPermission()) {
            loadContacts()
        } else {
            uiState = ShowRequestPermissionDialogUiState
        }
    }

    fun handleEvent(event: ImportContactsUserEvent) {
        when (event) {
            is AddButtonPressedEvent -> onAddButtonPressed()
            is ContactSelectedEvent -> updateContactSelectedStatus(event.contactId, event.selected)
            is ContactPermissionGrantedEvent -> loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            runCatching { uiStateMapper.map(getContactsUseCase.execute(), emptySet()) }
                    .onFailure { uiState = handleException(it) }
                    .onSuccess { uiState = it }

        }
    }

    private fun handleException(exception: Throwable): ImportContactsUiState {
        Timber.e(exception)
        return ErrorUiState(errorMessage = R.string.generic_error_message)
    }

    private fun onAddButtonPressed() {
        viewModelScope.launch {
            runCatching {
                val contactsToAdd: Set<Long> = selectedContacts
                uiState = LoadingUiState

                if (contactsToAdd.isEmpty()) {
                    throw IllegalStateException("Attempting to save with no contacts selected")
                }
                savePlayersUseCase.execute(contactsToAdd)
            }
                    .onFailure { uiState = handleException(it) }
                    .onSuccess { uiState = TerminalUiState }
        }
    }


    private fun updateContactSelectedStatus(contactId: Long, selected: Boolean) {
        val contacts: MutableList<ContactItemUiState> = uiState.safeContacts.toMutableList()
        val index = contacts.indexOfFirst { it.contactId == contactId }
        if (index != -1) {
            val updatedList = contacts
                    .apply {
                        this[index] = this[index].copy(isSelected = selected)
                    }
            uiState = ContactsListUiState(
                    contacts = updatedList,
                    importContactsButtonEnabled = updatedList.any { it.isSelected }
            )
        } else {
            Timber.e("Could not update contact [$contactId] to [$selected]")
        }
    }


}
