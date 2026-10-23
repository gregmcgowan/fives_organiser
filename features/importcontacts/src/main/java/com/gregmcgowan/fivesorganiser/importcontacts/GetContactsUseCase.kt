package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

interface GetContactsUseCase {
    suspend fun execute(): List<Contact>
}

@Inject
class GetContactsUseCaseImpl constructor(
    private val playersRepo: PlayerRepo,
    private val contactsImporter: ContactImporter,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GetContactsUseCase {
    override suspend fun execute(): List<Contact> =
        withContext(coroutineDispatchers.io) {
            val alreadyAddedContacts =
                playersRepo
                    .getPlayers()
                    .map { it.contactId }
                    .toSet()

            contactsImporter
                .getAllContacts()
                .filter { !alreadyAddedContacts.contains(it.contactId) }
        }
}
