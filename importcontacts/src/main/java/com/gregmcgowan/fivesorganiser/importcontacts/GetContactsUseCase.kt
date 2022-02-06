package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GetContactsUseCase {
    suspend fun execute(): List<Contact>
}

class GetContactsUseCaseImpl @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter,
        private val coroutineDispatchers: CoroutineDispatchers
) : GetContactsUseCase {

    override suspend fun execute(): List<Contact> =
            withContext(coroutineDispatchers.io) {
                filterContacts(playersRepo.getPlayers(), contactsImporter.getAllContacts())
            }

    private fun filterContacts(players: List<Player>, contacts: List<Contact>): List<Contact> {
        val alreadyAddedContacts = players.map { it.contactId }.toSet()
        return contacts.filter { !alreadyAddedContacts.contains(it.contactId) }
    }

}
