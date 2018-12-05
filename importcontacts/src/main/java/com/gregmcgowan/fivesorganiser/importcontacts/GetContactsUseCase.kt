package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter
) {

    suspend fun execute(): Either<Exception, List<Contact>> {
        // TODO should this be surrounded by a try catch clause?
        val contacts = filterContacts(playersRepo.getPlayers(), contactsImporter.getAllContacts())
        return Either.Right(contacts)
    }

    private fun filterContacts(
            players: List<Player>,
            contacts: List<Contact>
    ): List<Contact> {
        val alreadyAddedContacts = players.map { it.contactId }.toSet()
        return contacts.filter { !alreadyAddedContacts.contains(it.contactId) }
    }

}