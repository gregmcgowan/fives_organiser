package com.gregmcgowan.fivesorganiser.players.import

import rx.Observable

/**
 * Created by gregmcgowan on 17/01/2017.
 */
interface ContactImporter {

    fun getAllContacts() : Observable<List<Contact>>


}