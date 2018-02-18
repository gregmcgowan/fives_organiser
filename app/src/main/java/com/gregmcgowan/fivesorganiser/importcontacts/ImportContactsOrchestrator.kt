package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.data.player.Player
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepo
import javax.inject.Inject

class ImportContactsOrchestrator @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter
) {

    suspend fun saveSelectedContacts(selectedContacts: Set<Long>) {
        contactsImporter.getAllContacts()
                .filter { contact -> selectedContacts.contains(contact.contactId) }
                .map { contact ->
                    playersRepo.addPlayer(
                            contact.name,
                            contact.emailAddress,
                            contact.phoneNumber,
                            contact.contactId
                    )
                }
    }

    suspend fun getContacts(): List<Contact> {
        return filterContacts(playersRepo.getPlayers(), contactsImporter.getAllContacts())
    }

    private fun filterContacts(
            players: List<Player>,
            contacts: List<Contact>
    ): MutableList<Contact> {
        val filteredContacts = mutableListOf<Contact>()
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

        return filteredContacts
    }

}