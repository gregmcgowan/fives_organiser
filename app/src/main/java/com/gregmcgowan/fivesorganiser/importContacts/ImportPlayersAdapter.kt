package com.gregmcgowan.fivesorganiser.importContacts

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ContactItemUiModel
import com.gregmcgowan.fivesorganiser.importContacts.ImportContactsContract.ImportContactsUiEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class ImportPlayersAdapter : RecyclerView.Adapter<ImportPlayersAdapter.ContactViewHolder>() {

    private var contactList: List<ContactItemUiModel> = ArrayList()

    private var contactSelectedObservable: PublishSubject<ImportContactsUiEvent>
            = PublishSubject.create()

    fun setContacts(contacts: List<ContactItemUiModel>) {
        this.contactList = contacts
        val calculateDiff = DiffUtil.calculateDiff(DiffUtilCallback(contactList, contacts))
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ContactViewHolder?, position: Int) {
        val contact = contactList[position]

        holder?.let {
            holder.contactName.text = contact.name
            holder.contactCheckBox.setOnCheckedChangeListener { _, selected ->
                if (selected) {
                    contactSelectedObservable.onNext(
                            ImportContactsUiEvent.ContactSelected(contactId = contact.contactId)
                    )
                } else {
                    contactSelectedObservable.onNext(
                            ImportContactsUiEvent.ContactDeselected(contactId = contact.contactId)
                    )
                }
            }
            holder.contactCheckBox.isChecked = contact.isSelected
        }
    }

    fun contactSelectedObservable(): Observable<ImportContactsUiEvent> {
        return contactSelectedObservable;
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.import_contacts_list_item, parent, false))
    }

    class ContactViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView by find(R.id.import_contacts_list_item_name)
        val contactCheckBox: CheckBox by find(R.id.import_contacts_checkbox)

    }
}