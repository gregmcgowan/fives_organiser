package com.gregmcgowan.fivesorganiser.importContacts


interface ContactImporter {
    suspend fun getAllContacts(): List<Contact>
}