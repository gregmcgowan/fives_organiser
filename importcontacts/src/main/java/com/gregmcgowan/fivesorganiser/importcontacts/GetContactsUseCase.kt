package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter,
        private val coroutineDispatchers: CoroutineDispatchers
) {

    suspend fun execute(): Either<Exception, List<Contact>> =
            // TODO should this be surrounded by a try catch clause?
            withContext(coroutineDispatchers.io) {
                Either.Right(filterContacts(playersRepo.getPlayers(), contactsImporter.getAllContacts()))
            }

    private fun filterContacts(
            players: List<Player>,
            contacts: List<Contact>
    ): List<Contact> {
        val alreadyAddedContacts = players.map { it.contactId }.toSet()
        return contacts.filter { !alreadyAddedContacts.contains(it.contactId) }
    }

}