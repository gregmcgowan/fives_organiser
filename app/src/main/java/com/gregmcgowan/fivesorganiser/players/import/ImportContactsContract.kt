package com.gregmcgowan.fivesorganiser.players.import


interface ImportContactsContract {

    interface View {
        fun showContacts(contacts : List<Contact>)
        fun showProgress(show : Boolean)
        fun showMainContent(show : Boolean)
        fun showContactsError(exception: Exception)
        fun setContactItemListener(listener: ContactItemListener)
        fun setAddButtonEnabled(enabled : Boolean)
        fun closeScreen()
        fun returnToPlayersScreen()
    }

    interface Presenter {
        fun startPresenting()
        fun handleAddButtonPressed()
    }


    interface ContactItemListener {
        fun contactSelected(contactId: Int)
        fun contactDeselected(contactId: Int)
        fun getSelectedItems() : List<Int>
    }

}