package com.gregmcgowan.fivesorganiser.players.import

import com.gregmcgowan.fivesorganiser.players.PlayerRepo
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by gregmcgowan on 05/02/2017.
 */
class ImportContactsPresenter(val importContactsView: ImportContactsContract.View,
                              val playersRepo: PlayerRepo,
                              val contactsImporter: ContactImporter) : ImportContactsContract.Presenter,
                                                                        ImportContactsContract.ContactItemListener {

    var selectedItems : ArrayList<Int> = ArrayList()

    override fun contactSelected(contactId: Int) {
        selectedItems.add(contactId)
    }

    override fun contactDeselected(contactId: Int) {
        selectedItems.remove(contactId)
    }


    override fun startPresenting() {
        importContactsView.setContactItemListener(this)

        importContactsView.showProgress(true)
        importContactsView.showContactList(false)

        contactsImporter.getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ contacts ->
                    importContactsView.showContacts(contacts)
                    importContactsView.showProgress(false)
                    importContactsView.showContactList(true)
                }, { e ->
                    importContactsView.showProgress(false)
                    importContactsView.showContactList(false)
                    importContactsView.showContactsError(e as Exception) })


    }

    override fun getSelectedItems(): List<Int> {
        return selectedItems
    }
}