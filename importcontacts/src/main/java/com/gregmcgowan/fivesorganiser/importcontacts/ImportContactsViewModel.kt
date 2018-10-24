package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.annotation.MainThread
import com.gregmcgowan.fivesorganiser.core.Dispatchers
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import timber.log.Timber
import javax.inject.Inject

class ImportContactsViewModel @Inject constructor(
        private val mapper: ImportContactsUiModelMapper,
        private val orchestrator: ImportContactsOrchestrator,
        hasContactPermission: Boolean,
        dispatchers: Dispatchers
) : CoroutinesViewModel(dispatchers) {

    private lateinit var contacts: List<Contact>

    private val selectedContacts: MutableSet<Long> = mutableSetOf()

    val contactUiModelLiveData: LiveData<ImportContactsUiModel>
        get() = _contactUiModelLiveData

    private val _contactUiModelLiveData = MutableLiveData<ImportContactsUiModel>()

    val contactUiNavLiveData: LiveData<ImportContactsNavEvent>
        get() = _contactUiNavLiveData

    private val _contactUiNavLiveData = MutableLiveData<ImportContactsNavEvent>()

    init {
        _contactUiModelLiveData.value = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = false,
                showErrorMessage = false,
                errorMessage = null

        )

        if (hasContactPermission) {
            _contactUiNavLiveData.value = ImportContactsNavEvent.Idle
            loadContacts()
        } else {
            _contactUiNavLiveData.value = ImportContactsNavEvent.RequestPermission
        }
    }

    fun loadContacts() {
        launch(
                backgroundBlock = {
                    contacts = orchestrator.getContacts()
                    mapper.map(contacts, selectedContacts)
                },
                uiBlock = this::setUiModel
        )
    }

    fun onAddButtonPressed() {
        _contactUiModelLiveData.value = _contactUiModelLiveData.requireValue()
                .copy(showLoading = true, showContent = false)

        launch(
                backgroundBlock = { orchestrator.saveSelectedContacts(selectedContacts) },
                uiBlock = { _contactUiNavLiveData.value = ImportContactsNavEvent.CloseScreen }
        )
    }

    fun contactSelected(contactId: Long) {
        selectedContacts.add(contactId)
        setUiModel(mapper.map(contacts, selectedContacts))
    }

    fun contactDeselected(contactId: Long) {
        selectedContacts.remove(contactId)
        setUiModel(mapper.map(contacts, selectedContacts))
    }

    @MainThread
    private fun setUiModel(newUiModel: ImportContactsUiModel) {
        Timber.d("Setting contact list UI model to ${_contactUiModelLiveData.value}")
        _contactUiModelLiveData.value = newUiModel
    }


}
