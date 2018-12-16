package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import timber.log.Timber
import javax.inject.Inject

class ImportContactsViewModel @Inject constructor(
        private val mapper: ImportContactsUiModelMapper,
        private val savePlayersUseCase: SavePlayersUseCase,
        private val getContactsUseCase: GetContactsUseCase,
        hasContactPermission: Boolean,
        coroutineDisptachersAndContext: CoroutineDisptachersAndContext
) : CoroutinesViewModel(coroutineDisptachersAndContext) {

    val contactUiModelLiveData: LiveData<ImportContactsUiModel>
        get() = _contactUiModelLiveData
    private val _contactUiModelLiveData = MutableLiveData<ImportContactsUiModel>()

    val contactUiNavLiveData: LiveData<ImportContactsNavEvent>
        get() = _contactUiNavLiveData
    private val _contactUiNavLiveData = MutableLiveData<ImportContactsNavEvent>()

    private val selectedContacts: Set<Long>
        get() {
            return _contactUiModelLiveData.requireValue()
                    .contacts
                    .filter { it.isSelected }
                    .map { it.contactId }
                    .toSet()
        }

    init {
        _contactUiModelLiveData.value = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = false
        )

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
                    getContactsUseCase.execute().either(
                            { handleException(it) },
                            { mapper.map(it, selectedContacts) }
                    )

                },
                uiBlock = {
                    Timber.d("Setting contact list UI model to $it")
                    _contactUiModelLiveData.value = it
                }
        )
    }

    private fun handleException(exception: Exception): ImportContactsUiModel {
        TODO("Log exception and show retry button")
    }

    fun onAddButtonPressed() {
        _contactUiModelLiveData.value = _contactUiModelLiveData.requireValue()
                .copy(showLoading = true, showContent = false)
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

    private fun handleSavingException(it: java.lang.Exception): ImportContactsNavEvent {
        TODO("Log exception and show content again")
    }

    fun contactSelected(contactId: Long) {
        updateContactSelectedStatus(contactId, true)
    }

    fun contactDeselected(contactId: Long) {
        updateContactSelectedStatus(contactId, false)
    }

    private fun updateContactSelectedStatus(contactId: Long, selected: Boolean) {
        val currentUiModel = _contactUiModelLiveData.requireValue()
        val currentContacts = currentUiModel.contacts

        val indexOfFirst = currentContacts.indexOfFirst { it.contactId == contactId }
        if (indexOfFirst != -1) {
            val newContactItem = currentContacts[indexOfFirst].copy(isSelected = selected)
            val updatedContactItemList = currentContacts.toMutableList().apply {
                this[indexOfFirst] = newContactItem
            }
            val addButtonEnabled = updatedContactItemList.indexOfFirst { it.isSelected } != -1

            _contactUiModelLiveData.value = currentUiModel
                    .copy(
                            contacts = updatedContactItemList,
                            importContactsButtonEnabled = addButtonEnabled
                    )
        } else {
            //TODO log that we cannot find the contact to udpate
        }
    }


}
