package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class SavePlayersUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter,
        private val couroutineDispatchers: CoroutineDispatchers
) {

    suspend fun execute(selectedContacts: Set<Long>): Either<Exception, Unit> = withContext(couroutineDispatchers.io) {
        // TODO should this be surrounded by a try catch and return the exception as either?
        try {
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
        } catch (exception : Exception ){
            Either.Left(exception)
        }
        Either.Right(Unit)
    }
}