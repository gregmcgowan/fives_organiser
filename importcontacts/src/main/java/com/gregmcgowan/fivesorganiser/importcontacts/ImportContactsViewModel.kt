package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.CloseScreen
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.RequestPermission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImportContactsViewModel @Inject constructor(
        private val uiModelMapper: ImportContactsUiModelMapper,
        private val savePlayersUseCase: SavePlayersUseCase,
        private val getContactsUseCase: GetContactsUseCase,
        contactsPermission: Permission
) : ViewModel() {

    var uiModel: ImportContactsUiModel by mutableStateOf(
            value = ImportContactsUiModel(
                    contacts = emptyList(),
                    showLoading = true,
                    showContent = false,
                    importContactsButtonEnabled = false
            ))
        private set

    val importContactsUiEvent: Flow<ImportContactsUiEvent>
        get() = _importContactsUiEvent.asSharedFlow()

    private val _importContactsUiEvent = MutableSharedFlow<ImportContactsUiEvent>()

    private val selectedContacts: Set<Long>
        get() = uiModel.contacts
                .filter { it.isSelected }
                .map { it.contactId }
                .toSet()

    init {
        if (contactsPermission.hasPermission()) {
            loadContacts()
        } else {
            emitEvent(RequestPermission)
        }
    }

    fun handleEvent(importContactsEvent: ImportContactsUserEvent) {
        when (importContactsEvent) {
            is AddButtonPressedEvent -> onAddButtonPressed()
            is ContactSelectedEvent -> updateContactSelectedStatus(
                    importContactsEvent.contactId,
                    importContactsEvent.selected
            )
            is ContactPermissionGrantedEvent -> loadContacts()
        }
    }

    private fun emitEvent(importContactsEvent: ImportContactsUiEvent) {
        viewModelScope.launch {
            _importContactsUiEvent.emit(importContactsEvent)
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            uiModel = getContactsUseCase
                    .execute()
                    .either(
                            { handleException(it) },
                            { uiModelMapper.map(it, selectedContacts) }
                    )
        }
    }

    private fun handleException(exception: Exception): ImportContactsUiModel {
        Timber.e(exception)
        return uiModel.copy(
                showLoading = false,
                showContent = false,
                errorMessage = R.string.generic_error_message
        )
    }

    private fun onAddButtonPressed() {
        uiModel = uiModel.copy(showLoading = true, showContent = false)

        viewModelScope.launch {
            val event = savePlayersUseCase
                    .execute(selectedContacts)
                    .either(
                            { handleSavingException(it) },
                            { CloseScreen }
                    )
            emitEvent(event)
        }
    }

    private fun handleSavingException(exception: Exception): ImportContactsUiEvent {
        Timber.e(exception)
        uiModel = uiModel.copy(showLoading = false, showContent = true)
        return ImportContactsUiEvent.Idle
    }

    private fun updateContactSelectedStatus(contactId: Long, selected: Boolean) {
        val index = uiModel.contacts.indexOfFirst { it.contactId == contactId }
        if (index != -1) {
            val updatedList = uiModel.contacts
                    .toMutableList()
                    .apply {
                        this[index] = this[index].copy(isSelected = selected)
                    }
            uiModel = uiModel.copy(
                    contacts = updatedList,
                    importContactsButtonEnabled = updatedList.any { it.isSelected }
            )
        } else {
            Timber.e("Could not update contact [$contactId] to [$selected]")
        }
    }


}
