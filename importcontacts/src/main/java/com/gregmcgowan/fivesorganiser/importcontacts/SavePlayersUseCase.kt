package com.gregmcgowan.fivesorganiser.importcontacts

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import javax.inject.Inject

class SavePlayersUseCase @Inject constructor(
        private val playersRepo: PlayerRepo,
        private val contactsImporter: ContactImporter
) {

    suspend fun execute(selectedContacts: Set<Long>) {
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
}