package com.gregmcgowan.fivesorganiser.players.import

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import rx.Observable
import rx.Observable.defer
import rx.Observable.just
import java.util.*

class AndroidContactImporter(val contentResolver: ContentResolver) : ContactImporter {

    val PROJECTION = arrayOf(Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.SORT_KEY_PRIMARY)


    override fun getAllContacts(): Observable<List<Contact>> {


        // they are not displayed
//        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
//            + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
//            + Contacts.DISPLAY_NAME + " != '' ) AND ("
//            + Contacts._ID + " not in (" + existingPlayerId + ")))";
//        Log.d(FivesOrganiserConstants.TAG, select);
//        contactsCursor = contentResolver.query(Contacts.CONTENT_URI,
//            CONTACTS_SUMMARY_PROJECTION, select, null,
//            Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

        val selectString = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.HAS_PHONE_NUMBER + "=1) AND (" + Contacts.DISPLAY_NAME + " != '' ) )"

        return defer {
            just(contentResolver.query(Contacts.CONTENT_URI,
                    PROJECTION, selectString, null,
                    Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC")
            )
        }.flatMap { cursor ->
            just(createContactList(cursor))
        }
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

            return Contact(name, phoneNumber, contactId.toInt())
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

//
//    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//    int numberOfEmails = emailCursor.getCount();
//    String email = null;
//    if (numberOfEmails > 0) {
//        emailCursor.moveToFirst();
//        email = DbUtils.getStringFromCursor(emailCursor, ContactsContract.CommonDataKinds.Email.ADDRESS);
//    }
}