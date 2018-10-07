package com.gregmcgowan.fivesorganiser.importcontacts


interface ContactImporter {
    suspend fun getAllContacts(): List<Contact>
}