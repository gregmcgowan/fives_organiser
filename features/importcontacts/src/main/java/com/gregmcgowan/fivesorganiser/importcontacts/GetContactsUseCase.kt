package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
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
                val alreadyAddedContacts = playersRepo.getPlayers()
                        .map { it.contactId }
                        .toSet()

                contactsImporter.getAllContacts()
                        .filter { !alreadyAddedContacts.contains(it.contactId) }
            }

}
