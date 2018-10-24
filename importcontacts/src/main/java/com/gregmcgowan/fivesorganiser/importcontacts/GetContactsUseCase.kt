package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter
) {

    suspend fun execute(): List<Contact> {
        return filterContacts(playersRepo.getPlayers(), contactsImporter.getAllContacts())
    }

    private fun filterContacts(
            players: List<Player>,
            contacts: List<Contact>
    ): List<Contact> {
        val alreadyAddedContacts = players.map { it.contactId }.toSet()
        return contacts.filter { !alreadyAddedContacts.contains(it.contactId) }
    }

}