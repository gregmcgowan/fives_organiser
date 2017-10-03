package com.gregmcgowan.fivesorganiser.importContacts

import io.reactivex.Single


interface ContactImporter {

    fun getAllContacts(): Single<List<Contact>>

}