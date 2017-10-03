package com.gregmcgowan.fivesorganiser.importContacts

import android.arch.lifecycle.LifecycleObserver
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsUiModel
import io.reactivex.Observable

typealias ImportContactsUiModelReducer = (ImportContactsUiModel) -> ImportContactsUiModel

interface ImportContactsContract {

    interface Ui {
        fun contactListEvents(): Observable<ImportContactsUiEvent>
        fun importContactPressedEvents(): Observable<ImportContactsUiEvent>
        fun render(uiModel: ImportContactsUiModel)
    }

    interface Presenter : LifecycleObserver {
        fun startPresenting()
        fun stopPresenting()
    }

    sealed class ImportContactsUiEvent {
        class ContactSelected(val contactId: Int) : ImportContactsUiEvent()
        class ContactDeselected(val contactId: Int) : ImportContactsUiEvent()
        class ImportContactsPressed : ImportContactsUiEvent()
        class UiShown : ImportContactsUiEvent()
    }

    sealed class ImportContactsAction {
        class ImportStarted : ImportContactsAction()
        class ImportFinished : ImportContactsAction()
    }

    data class ImportContactsUiModel(
            val contacts: List<ContactItemUiModel>,
            val showLoading: Boolean,
            val showContent: Boolean,
            val importContactsButtonEnabled: Boolean,
            val showErrorMessage: Boolean,
            val errorMessage: String?,
            val closeScreen : Boolean = false
    )

    data class ContactItemUiModel(
            val name: String,
            val isSelected: Boolean,
            val contactId: Int
    )

}