package com.gregmcgowan.fivesorganiser.players.import

import com.gregmcgowan.fivesorganiser.core.ViewPresenter
import com.gregmcgowan.fivesorganiser.core.ViewState


interface ImportContactsContract {

    interface View {
        var viewState : ViewState

        fun setContactItemListener(listener: ContactItemListener)
        fun setAddButtonEnabled(enabled: Boolean)
        fun closeScreen()
        fun returnToPlayersScreen()
    }

    interface Presenter : ViewPresenter{
        fun handleAddButtonPressed()
    }


    interface ContactItemListener {
        fun contactSelected(contactId: Int)
        fun contactDeselected(contactId: Int)
        fun getSelectedItems(): List<Int>
    }

    data class ImportContactsModel(val contacts : List<Contact>)

}