package com.gregmcgowan.fivesorganiser.importcontacts

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import javax.inject.Inject

class AndroidContactImporter @Inject constructor(
    private val contentResolver: ContentResolver,
) : ContactImporter {
    override suspend fun getAllContacts(): List<Contact> {
        val selectString =
            "((" + Contacts.DISPLAY_NAME +
                " NOTNULL) AND (" +
                Contacts.HAS_PHONE_NUMBER + "=1) AND (" +
                Contacts.DISPLAY_NAME + " != '' ) )"

        val cursor =
            contentResolver.query(
                Contacts.CONTENT_URI,
                arrayOf(
                    Contacts._ID,
                    Contacts.LOOKUP_KEY,
                    Contacts.DISPLAY_NAME_PRIMARY,
                    Contacts.SORT_KEY_PRIMARY,
                ),
                selectString,
                null,
                Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC",
            )
        return cursor?.let { createContactList(cursor) } ?: emptyList()
    }

    private fun createContactList(cursor: Cursor): ArrayList<Contact> {
        cursor.moveToFirst()
        val contactList = ArrayList<Contact>()
        (0 until cursor.count).forEach { _ ->
            val contact = createContact(cursor)
            if (contact != null) {
                contactList.add(contact)
            }
            cursor.moveToNext()
        }
        cursor.close()
        return contactList
    }

    private fun createContact(cursor: Cursor): Contact? {
        val contactId = safeGetString(cursor, Contacts._ID) ?: return null

        val phonesCursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                arrayOf(contactId),
                null,
            )

        phonesCursor?.moveToFirst()
        val name =
            safeGetString(
                cursor = phonesCursor,
                columnName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            )
        val phoneNumber =
            safeGetString(
                cursor = phonesCursor,
                columnName = ContactsContract.CommonDataKinds.Phone.NUMBER,
            )

        phonesCursor?.close()
        return if (name != null) {
            // TODO actually add email address
            Contact(
                name = name,
                phoneNumber = phoneNumber,
                emailAddress = null,
                contactId = contactId.toLong(),
            )
        } else {
            null
        }
    }
}

private fun safeGetString(
    cursor: Cursor?,
    columnName: String,
): String? {
    val columnIndex = cursor?.getColumnIndex(columnName) ?: -1
    if (columnIndex != -1 && cursor != null && cursor.count > 0) {
        return cursor.getString(columnIndex)
    }
    return null
}
