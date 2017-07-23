package com.gregmcgowan.fivesorganiser.players.import

import com.gregmcgowan.fivesorganiser.core.ViewState
import com.gregmcgowan.fivesorganiser.core.ViewState.*
import com.gregmcgowan.fivesorganiser.players.PlayerRepo
import com.gregmcgowan.fivesorganiser.players.import.ImportContactsContract.ImportContactsModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ImportContactsPresenter(val importContactsView: ImportContactsContract.View,
                              val playersRepo: PlayerRepo,
                              val contactsImporter: ContactImporter) : ImportContactsContract.Presenter,
        ImportContactsContract.ContactItemListener {

    var selectedItems: ArrayList<Int> = ArrayList()
    var disposables: CompositeDisposable = CompositeDisposable()

    override fun handleAddButtonPressed() {
        setViewState(Loading())
        val disposable = contactsImporter.getAllContacts()
                .toObservable()
                .flatMapIterable { l -> l }
                .filter { contact -> selectedItems.contains(contact.contactId) }
                .flatMap { (name, phoneNumber, emailAddress, contactId) ->
                    Observable.just(playersRepo.addPlayer(name,
                            emailAddress,
                            phoneNumber,
                            contactId
                    ))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handlePlayerCreated,
                        this::handleError,
                        importContactsView::returnToPlayersScreen)
        disposables.add(disposable)

    }

    private fun handlePlayerCreated(it: Unit) {
        //TODO
    }

    private fun handleError(t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contactSelected(contactId: Int) {
        selectedItems.add(contactId)
        importContactsView.setAddButtonEnabled(true)
    }

    override fun contactDeselected(contactId: Int) {
        selectedItems.remove(contactId)

        if (selectedItems.size == 0) {
            importContactsView.setAddButtonEnabled(false)
        }
    }

    override fun startPresenting() {
        importContactsView.setAddButtonEnabled(false)
        importContactsView.setContactItemListener(this)

        importContactsView.viewState = Loading()

        val disposable = contactsImporter.getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setViewState(Success(ImportContactsModel(it))) },
                        { _ -> setViewState(Error("Error getting contacts")) })
        disposables.add(disposable)
    }

    private fun setViewState(viewState: ViewState) {
        importContactsView.viewState = viewState
    }

    override fun stopPresenting() {
        disposables.clear()
    }

    override fun getSelectedItems(): List<Int> {
        return selectedItems
    }
}