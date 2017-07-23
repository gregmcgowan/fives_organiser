package com.gregmcgowan.fivesorganiser.players.import

import io.reactivex.Single


interface ContactImporter {

    fun getAllContacts(): Single<List<Contact>>

}