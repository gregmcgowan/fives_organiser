package com.gregmcgowan.fivesorganiser.importcontacts

data class Contact(val name: String = "",
                   val phoneNumber: String = "",
                   val emailAddress: String = "",
                   val contactId: Long)


interface ContactImporter {
    suspend fun getAllContacts(): List<Contact>
}
