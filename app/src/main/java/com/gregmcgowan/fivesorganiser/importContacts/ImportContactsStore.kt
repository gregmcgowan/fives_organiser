package com.gregmcgowan.fivesorganiser.importContacts

import com.gregmcgowan.fivesorganiser.core.data.player.PlayerEntity
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class ImportContactsStore(private val playersRepo: PlayerRepo,
                          private val contactsImporter: ContactImporter) {

    private val selectedContacts: MutableSet<Int> = mutableSetOf()


    fun saveSelectedContacts(): Completable {
        return contactsImporter.getAllContacts()
                .toObservable()
                .flatMapIterable { l -> l }
                .filter { contact -> selectedContacts.contains(contact.contactId) }
                .flatMap { (name, phoneNumber, emailAddress, contactId) ->
                    Observable.just(playersRepo.addPlayer(
                            name,
                            emailAddress,
                            phoneNumber,
                            contactId
                    ))
                }
                .toList()
                .toCompletable()
    }

    fun addContact(contactID: Int): Single<Set<Int>> {
        selectedContacts.add(contactID)
        return Single.just(selectedContacts)
    }

    fun removeContact(contactID: Int): Single<Set<Int>> {
        selectedContacts.remove(contactID)
        return Single.just(selectedContacts)
    }

    fun getSelectedContacts(): Single<Set<Int>> {
        return Single.just(selectedContacts)
    }

    fun getContacts(): Single<List<Contact>> {
        return Single.zip(contactsImporter.getAllContacts(), playersRepo.getPlayers(), filterPlayers())
    }

    private fun filterPlayers(): BiFunction<List<Contact>, List<PlayerEntity>, List<Contact>> {
        return BiFunction { contacts: List<Contact>, players: List<PlayerEntity> ->
            val filteredContacts = mutableListOf<Contact>()
            if (players.isNotEmpty()) {
                for (contact in contacts) {
                    var alreadyAdded = false
                    for (player in players) {
                        alreadyAdded = contact.contactId == player.contactId
                        if (alreadyAdded) {
                            break
                        }
                    }
                    if (!alreadyAdded) {
                        filteredContacts.add(contact)
                    }
                }

            } else {
                filteredContacts.addAll(contacts)
            }
            filteredContacts
        }
    }


}