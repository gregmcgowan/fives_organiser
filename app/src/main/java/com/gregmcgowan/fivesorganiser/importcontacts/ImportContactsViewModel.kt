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
        coroutineContexts: CoroutineContexts,
        private val orchestrator: ImportContactsOrchestrator
) : CoroutinesViewModel(coroutineContexts) {

    private val selectedContacts: MutableSet<Long> = mutableSetOf()
    private val contactUiModelLiveData = MutableLiveData<ImportContactsUiModel>()
    private val contactUiNavLiveData = MutableLiveData<ImportContactsNavEvent>()
            get

    init {
        contactUiModelLiveData.value = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = false,
                showErrorMessage = false,
                errorMessage = null

        )
        contactUiNavLiveData.value = ImportContactsNavEvent.Idle
    }


    fun uiModel(): LiveData<ImportContactsUiModel> {
        return contactUiModelLiveData
    }

    fun navEvents(): LiveData<ImportContactsNavEvent> {
        return contactUiNavLiveData
    }

    fun onViewShown() {
        contactUiNavLiveData.value = ImportContactsNavEvent.Idle
        updateUiModel(loadingUiModel())
        runAndUpdateUiModel({ contactListUiModel(orchestrator.getContacts(), selectedContacts) })
    }

    private fun runAndUpdateUiModel(reducer: suspend () -> ImportContactsUiModelReducer) {
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { reducer().invoke(getUiModel()) },
                uiBlock = { uiModel -> setUiModel(uiModel) }
        )
    }

    fun onAddButtonPressed() {
        updateUiModel(loadingUiModel())
        runOnBackgroundAndUpdateOnUI(
                backgroundBlock = { orchestrator.saveSelectedContacts(selectedContacts) },
                uiBlock = { contactUiNavLiveData.value = ImportContactsNavEvent.CloseScreen }
        )
    }

    fun contactSelected(contactId: Long) {
        selectedContacts.add(contactId)
        updateUiModel(contactSelectedUiModel(contactId))
    }

    fun contactDeselected(contactId: Long) {
        selectedContacts.remove(contactId)
        updateUiModel(contactDeselectedUiModel(contactId, selectedContacts))
    }

    private fun updateUiModel(reducer: ImportContactsUiModelReducer) {
        Timber.d("Setting contact list UI model to ${contactUiModelLiveData.value}")
        setUiModel(reducer.invoke(contactUiModelLiveData.requireValue()))
    }

    @MainThread
    private fun setUiModel(newUiModel: ImportContactsUiModel) {
        contactUiModelLiveData.value = newUiModel
    }

    private suspend fun getUiModel(): ImportContactsUiModel {
        return onUiContext { contactUiModelLiveData.requireValue() }
    }

}
