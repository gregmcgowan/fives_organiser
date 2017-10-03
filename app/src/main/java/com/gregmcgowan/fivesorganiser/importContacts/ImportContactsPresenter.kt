package com.gregmcgowan.fivesorganiser.importContacts

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.*
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsAction.ImportFinished
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsAction.ImportStarted
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ImportContactsPresenter(private val importContactsUi: ImportContactsContract.Ui,
                              private val importContactsStore: ImportContactsStore) : ImportContactsContract.Presenter {


    private var disposables: CompositeDisposable = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun startPresenting() {
        val initialUiModel = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = false,
                showErrorMessage = false,
                errorMessage = null

        )
        val events = merge(
                listOf(Observable.just(
                        ImportContactsUiEvent.UiShown()),
                        importContactsUi.contactListEvents(),
                        importContactsUi.importContactPressedEvents())
                )
                .share()

        disposables.add(
                process(events, initialUiModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { uiModel -> importContactsUi.render(uiModel) }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun stopPresenting() {
        disposables.clear()
    }

    private fun process(events: Observable<ImportContactsUiEvent>,
                        initialUiModel: ImportContactsUiModel): Observable<ImportContactsUiModel> {
        return events
                .flatMap { event ->
                    when (event) {
                        is ImportContactsUiEvent.UiShown -> {
                            Single.zip(importContactsStore.getContacts(),
                                    importContactsStore.getSelectedContactsState(),
                                    BiFunction<List<Contact>, Set<Int>, ImportContactsUiModelReducer> { contacts, selectedContacts ->
                                        uiModelReducer(contacts, selectedContacts)
                                    }
                            )
                                    .subscribeOn(Schedulers.io())
                                    .toObservable()
                        }

                        is ImportContactsContract.ImportContactsUiEvent.ContactSelected -> {
                            importContactsStore.addContact(event.contactId)
                                    .map { contactSelectedReducer(event.contactId) }
                                    .toObservable()
                        }

                        is ImportContactsContract.ImportContactsUiEvent.ContactDeselected -> {
                            importContactsStore.removeContact(event.contactId)
                                    .map { contactDeselectedReducer(event.contactId, it) }
                                    .toObservable()
                        }

                        is ImportContactsContract.ImportContactsUiEvent.ImportContactsPressed -> {
                            importContactsStore.saveSelectedContacts()
                                    .subscribeOn(Schedulers.io())
                                    .andThen(Observable.just<ImportContactsAction?>(ImportFinished()))
                                    .startWith(Observable.just<ImportContactsAction?>(ImportStarted()))
                                    .map {
                                        when (it) {
                                            is ImportStarted -> loadingReducer()
                                            is ImportFinished -> importCompleteReducer()
                                        }
                                    }
                        }

                    }

                }
                .scan(initialUiModel, { previousUiModel, reducer -> reducer(previousUiModel) })
                .distinctUntilChanged()
                .doAfterNext({ model -> Timber.d("Rendering $model") })
    }


}