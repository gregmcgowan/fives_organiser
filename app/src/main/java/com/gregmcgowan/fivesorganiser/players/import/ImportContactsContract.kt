package com.gregmcgowan.fivesorganiser.players.import

/**
 * Created by gregmcgowan on 05/02/2017.
 */
interface ImportContactsContract {

    interface View {
        fun showContacts(contacts : List<Contact>)
        fun showProgress(show : Boolean)
        fun showContactList(show : Boolean)
        fun showContactsError(exception: Exception)
        fun setContactItemListener(listener: ContactItemListener)
    }

    interface Presenter {
        fun startPresenting()
    }


    interface ContactItemListener {
        fun contactSelected(contactId: Int)
        fun contactDeselected(contactId: Int)
        fun getSelectedItems() : List<Int>
    }

}