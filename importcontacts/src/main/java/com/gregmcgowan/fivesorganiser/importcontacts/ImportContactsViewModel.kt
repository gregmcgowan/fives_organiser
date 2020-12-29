package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import timber.log.Timber

class ImportContactsViewModel @ViewModelInject constructor(
        private val mapper: ImportContactsUiModelMapper,
        private val savePlayersUseCase: SavePlayersUseCase,
        private val getContactsUseCase: GetContactsUseCase,
        hasContactPermission: Boolean,
        coroutineDisptachersAndContext: CoroutineDisptachersAndContext
) : CoroutinesViewModel(coroutineDisptachersAndContext) {

    var uiModel: ImportContactsUiModel by mutableStateOf(
            value = ImportContactsUiModel(
                    contacts = emptyList(),
                    showLoading = true,
                    showContent = false,
                    importContactsButtonEnabled = false
            ))
        private set

    val contactUiNavLiveData: LiveData<ImportContactsNavEvent>
        get() = _contactUiNavLiveData
    private val _contactUiNavLiveData = MutableLiveData<ImportContactsNavEvent>()

    private val selectedContacts: Set<Long>
        get() = uiModel
                .contacts
                .filter { it.isSelected }
                .map { it.contactId }
                .toSet()

    init {
        start(hasContactPermission)
    }

    private fun start(hasContactPermission: Boolean) {
        if (hasContactPermission) {
            _contactUiNavLiveData.value = ImportContactsNavEvent.Idle
            loadContacts()
        } else {
            _contactUiNavLiveData.value = ImportContactsNavEvent.RequestPermission
        }
    }

    fun onContactsPermissionGranted() {
        loadContacts()
    }

    private fun loadContacts() {
        launch(
                backgroundBlock = {
                    getContactsUseCase
                            .execute()
                            .either(
                                    { handleException(it) },
                                    { mapper.map(it, selectedContacts) }
                            )

                },
                uiBlock = {
                    Timber.d("Setting contact list UI model to $it")
                    uiModel = it
                }
        )
    }

    private fun handleException(exception: Exception): ImportContactsUiModel {
        TODO("Log exception and show retry button")
    }

    fun onAddButtonPressed() {
        uiModel.copy(showLoading = true, showContent = false)

        launch(
                backgroundBlock = {
                    savePlayersUseCase.execute(selectedContacts).either(
                            { handleSavingException(it) },
                            { ImportContactsNavEvent.CloseScreen }
                    )
                },
                uiBlock = { _contactUiNavLiveData.value = it }
        )
    }

    private fun handleSavingException(it: Exception): ImportContactsNavEvent {
        TODO("Log exception and show content again")
    }

    fun updateContactSelectedStatus(contactId: Long, selected: Boolean) {
        val indexOfFirst = uiModel.contacts.indexOfFirst { it.contactId == contactId }
        if (indexOfFirst != -1) {
            val updatedContactItemList = uiModel.contacts.toMutableList().apply {
                this[indexOfFirst] = uiModel.contacts[indexOfFirst].copy(isSelected = selected)
            }
            val addButtonEnabled = updatedContactItemList.indexOfFirst { it.isSelected } != -1

            uiModel = uiModel.copy(
                    contacts = updatedContactItemList,
                    importContactsButtonEnabled = addButtonEnabled
            )
        } else {
            //TODO log that we cannot find the contact to udpate
        }
    }


}
