package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.core.runCatchingSafely
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.UserDeniedPermissionUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionDeniedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionGrantedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.DoNotTryPermissionAgainEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.TryPermissionAgainEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImportContactsViewModel @Inject constructor(
    private val savePlayersUseCase: SavePlayersUseCase,
    private val getContactsUseCase: GetContactsUseCase,
    contactsPermission: Permission,
) : ViewModel() {
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _uiState: MutableStateFlow<ImportContactsUiState> =
        MutableStateFlow(LoadingUiState)

    val uiState: StateFlow<ImportContactsUiState> = _uiState.asStateFlow()

    init {
        if (contactsPermission.hasPermission()) {
            loadContacts()
        } else {
            _uiState.update { ShowRequestPermissionDialogUiState }
        }
    }

    fun handleEvent(event: ImportContactsUserEvent) {
        when (event) {
            is AddButtonPressedEvent -> {
                onAddButtonPressed()
            }

            is ContactSelectedEvent -> {
                updateContactSelectedStatus(contactId = event.contactId, selected = event.selected)
            }

            is ContactPermissionGrantedEvent -> {
                loadContacts()
            }

            is ContactPermissionDeniedEvent -> {
                _uiState.update { UserDeniedPermissionUiState }
            }

            is DoNotTryPermissionAgainEvent -> {
                _uiState.update { TerminalUiState }
            }

            is TryPermissionAgainEvent -> {
                _uiState.update { ShowRequestPermissionDialogUiState }
            }
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            flow { emit(getContactsUseCase.execute()) }
                .combine(selectedIds) { contacts, selectedIds ->
                    ContactsListUiState(
                        contacts = contacts.map { it.toUiState(selectedIds) },
                        addContactsButtonEnabled = selectedIds.isNotEmpty(),
                    )
                }.catch { _uiState.value = handleException(it) }
                .collect { _uiState.value = it }
        }
    }

    private fun Contact.toUiState(selectedIds: Set<Long>): ContactItemUiState =
        with(this) {
            ContactItemUiState(
                name = this.name,
                contactId = this.contactId,
                isSelected = selectedIds.contains(this.contactId),
            )
        }

    private fun handleException(exception: Throwable): ImportContactsUiState {
        Timber.e(exception)
        return ErrorUiState(errorMessage = R.string.generic_error_message)
    }

    private fun onAddButtonPressed() {
        _uiState.update { LoadingUiState }
        viewModelScope.launch {
            runCatchingSafely {
                savePlayersUseCase.execute(selectedIds.value)
            }.onFailure { exception -> _uiState.update { handleException(exception) } }
                .onSuccess { _uiState.update { TerminalUiState } }
        }
    }

    private fun updateContactSelectedStatus(
        contactId: Long,
        selected: Boolean,
    ) {
        selectedIds.update {
            if (selected) it.plus(contactId) else it.minus(contactId)
        }
    }
}
