package com.gregmcgowan.fivesorganiser.importcontacts

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread
import com.gregmcgowan.fivesorganiser.core.CoroutineContexts
import com.gregmcgowan.fivesorganiser.core.CoroutinesViewModel
import com.gregmcgowan.fivesorganiser.core.requireValue
import timber.log.Timber
import javax.inject.Inject

class ImportContactsViewModel @Inject constructor(
        private val uiModelMapper: ImportContactsUiModelMapper,
        private val orchestrator: ImportContactsOrchestrator,
        hasContactPermission: Boolean,
        coroutineContexts: CoroutineContexts
) : CoroutinesViewModel(coroutineContexts) {

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
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = {
                    this.contacts = orchestrator.getContacts()
                    uiModelMapper.map(contacts, selectedContacts)
                },
                uiBlock = this::setUiModel
        )
    }

    fun onAddButtonPressed() {
        _contactUiModelLiveData.value = _contactUiModelLiveData.requireValue()
                .copy(showLoading = true, showContent = false)

        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { orchestrator.saveSelectedContacts(selectedContacts) },
                uiBlock = { _contactUiNavLiveData.value = ImportContactsNavEvent.CloseScreen }
        )
    }

    fun contactSelected(contactId: Long) {
        selectedContacts.add(contactId)
        setUiModel(uiModelMapper.map(contacts, selectedContacts))
    }

    fun contactDeselected(contactId: Long) {
        selectedContacts.remove(contactId)
        setUiModel(uiModelMapper.map(contacts, selectedContacts))
    }

    @MainThread
    private fun setUiModel(newUiModel: ImportContactsUiModel) {
        Timber.d("Setting contact list UI model to ${_contactUiModelLiveData.value}")
        _contactUiModelLiveData.value = newUiModel
    }


}
