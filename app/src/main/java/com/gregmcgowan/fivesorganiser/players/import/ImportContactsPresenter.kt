package com.gregmcgowan.fivesorganiser.players.import

import com.gregmcgowan.fivesorganiser.players.PlayerRepo
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class ImportContactsPresenter(val importContactsView: ImportContactsContract.View,
                              val playersRepo: PlayerRepo,
                              val contactsImporter: ContactImporter) : ImportContactsContract.Presenter,
        ImportContactsContract.ContactItemListener {

    var selectedItems: ArrayList<Int> = ArrayList()

    override fun handleAddButtonPressed() {
        contactsImporter.getAllContacts()
                .flatMapIterable { l -> l }
                .filter { contact -> selectedItems.contains(contact.contactId) }
                .flatMap { (name, phoneNumber, emailAddress, contactId) ->
                    Observable.just(playersRepo.addPlayer(name,
                            emailAddress,
                            phoneNumber,
                            contactId
                            ))
                }
                .toCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ -> importContactsView.returnToPlayersScreen() })

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
        importContactsView.showProgress(true)
        importContactsView.showMainContent(false)

        contactsImporter.getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ contacts ->
                    importContactsView.showContacts(contacts)
                    importContactsView.showProgress(false)
                    importContactsView.showMainContent(true)
                }, { e ->
                    importContactsView.showProgress(false)
                    importContactsView.showMainContent(false)
                    importContactsView.showContactsError(e as Exception)
                })


    }

    override fun getSelectedItems(): List<Int> {
        return selectedItems
    }
}