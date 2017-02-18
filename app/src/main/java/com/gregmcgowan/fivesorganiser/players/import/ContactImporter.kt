package com.gregmcgowan.fivesorganiser.players.import

import rx.Observable


interface ContactImporter {

    fun getAllContacts() : Observable<List<Contact>>

}