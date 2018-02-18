package com.gregmcgowan.fivesorganiser.importcontacts

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import java.util.*
import javax.inject.Inject

class AndroidContactImporter @Inject constructor(
        private val contentResolver: ContentResolver
) : ContactImporter {


    override suspend fun getAllContacts(): List<Contact> {
        val selectString = "((" + Contacts.DISPLAY_NAME +
                " NOTNULL) AND (" +
                Contacts.HAS_PHONE_NUMBER + "=1) AND (" +
                Contacts.DISPLAY_NAME + " != '' ) )"

        val cursor = contentResolver.query(
                Contacts.CONTENT_URI,
                arrayOf(Contacts._ID,
                        Contacts.LOOKUP_KEY,
                        Contacts.DISPLAY_NAME_PRIMARY,
                        Contacts.SORT_KEY_PRIMARY),
                selectString,
                null,
                Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC")
        return createContactList(cursor)
    }

    private fun createContactList(cursor: Cursor): ArrayList<Contact> {
        cursor.moveToFirst()
        val contactList = ArrayList<Contact>()
        for (index in 0..cursor.count - 1) {
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
        val contactId = cursor.getInt(cursor.getColumnIndex(Contacts._ID)).toString()

        val select = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE

        val phonesCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                select, arrayOf(contactId), null)

        if (phonesCursor.count > 0) {
            phonesCursor.moveToFirst()

            val phoneNumber = safeGetString(phonesCursor, ContactsContract.CommonDataKinds.Phone.NUMBER)
            val name = safeGetString(phonesCursor, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            phonesCursor.close()
            //TODO
            return Contact(name, phoneNumber, "", contactId.toLong())
        }

        phonesCursor.close()
        return null
    }

    private fun safeGetString(cursor: Cursor, columnName: String): String {
        val columnIndex = cursor.getColumnIndex(columnName)
        if (columnIndex != -1) {
            return cursor.getString(columnIndex)
        }
        return ""
    }

}