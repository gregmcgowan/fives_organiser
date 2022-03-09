package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SavePlayersUseCase {
    suspend fun execute(selectedContacts: Set<Long>)
}

class SavePlayersUseCaseImpl @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter,
        private val coroutineDispatchers: CoroutineDispatchers
) : SavePlayersUseCase {

    override suspend fun execute(selectedContacts: Set<Long>) = withContext(coroutineDispatchers.io) {
        contactsImporter.getAllContacts()
                .filter { contact -> selectedContacts.contains(contact.contactId) }
                .forEach { contact ->
                    playersRepo.addPlayer(
                            contact.name,
                            contact.emailAddress,
                            contact.phoneNumber,
                            contact.contactId
                    )
                }
    }
}
