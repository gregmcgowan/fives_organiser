package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import java.lang.Exception
import javax.inject.Inject

class SavePlayersUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter
) {

    suspend fun execute(selectedContacts: Set<Long>): Either<Exception, Unit> {
        // TODO should this be surrounded by a try catch and return the exception as either?
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
        return Either.Right(Unit)
    }
}