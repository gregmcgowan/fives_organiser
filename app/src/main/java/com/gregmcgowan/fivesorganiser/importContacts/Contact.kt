package com.gregmcgowan.fivesorganiser.importContacts

data class Contact(val name: String = "",
                   val phoneNumber: String = "",
                   val emailAddress : String = "",
                   val contactId: Int)