package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionGrantedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImportContactsViewModel @Inject constructor(
        private val uiStateMapper: ImportContactsUiStateMapper,
        private val savePlayersUseCase: SavePlayersUseCase,
        private val getContactsUseCase: GetContactsUseCase,
        contactsPermission: Permission
) : ViewModel() {

    private val mutableUiStateFlow: MutableStateFlow<ImportContactsUiState> =
            MutableStateFlow(LoadingUiState)

    val uiStateFlow: StateFlow<ImportContactsUiState> = mutableUiStateFlow.asStateFlow()

    init {
        if (contactsPermission.hasPermission()) {
            loadContacts()
        } else {
            mutableUiStateFlow.update { ShowRequestPermissionDialogUiState }
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
                    .onFailure { exception ->
                        mutableUiStateFlow.update { handleException(exception) }
                    }
                    .onSuccess { state -> mutableUiStateFlow.update { state } }
        }
    }

    private fun handleException(exception: Throwable): ImportContactsUiState {
        Timber.e(exception)
        return ErrorUiState(errorMessage = R.string.generic_error_message)
    }

    private fun onAddButtonPressed() {
        val previousUiState = mutableUiStateFlow.getAndUpdate { LoadingUiState }

        viewModelScope.launch {
            runCatching {
                val contactsToAdd: Set<Long> = previousUiState.safeContacts
                        .filter { it.isSelected }
                        .map { it.contactId }
                        .toSet()
                if (contactsToAdd.isEmpty()) {
                    throw IllegalStateException("Attempting to save with no contacts selected")
                }
                savePlayersUseCase.execute(contactsToAdd)
            }
                    .onFailure { exception -> mutableUiStateFlow.update { handleException(exception) } }
                    .onSuccess { mutableUiStateFlow.update { TerminalUiState } }
        }
    }


    private fun updateContactSelectedStatus(contactId: Long, selected: Boolean) {
        val contacts: MutableList<ContactItemUiState> = uiStateFlow.value
                .safeContacts.toMutableList()
        val index = contacts.indexOfFirst { it.contactId == contactId }
        if (index != -1) {
            val updatedList = contacts
                    .apply { this[index] = this[index].copy(isSelected = selected) }
            mutableUiStateFlow.update {
                ContactsListUiState(
                        contacts = updatedList,
                        addContactsButtonEnabled = updatedList.any { it.isSelected }
                )
            }
        } else {
            Timber.e("Could not update contact [$contactId] to [$selected]")
        }
    }


}
