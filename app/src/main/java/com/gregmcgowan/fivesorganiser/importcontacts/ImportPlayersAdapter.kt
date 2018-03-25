package com.gregmcgowan.fivesorganiser.importcontacts

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.core.find
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback

class ImportPlayersAdapter : RecyclerView.Adapter<ImportPlayersAdapter.ContactViewHolder>() {

    private var contactList: MutableList<ContactItemUiModel> = mutableListOf()

    var contactListInteractions: ContactListInteraction? = null

    fun setContacts(newContacts: List<ContactItemUiModel>) {
        val calculateDiff = DiffUtil.calculateDiff(DiffUtilCallback(contactList, newContacts,
                { c1, c2 -> c1.contactId == c2.contactId }))
        this.contactList.clear()
        this.contactList.addAll(newContacts)
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.contactName.text = contact.name
        holder.contactCheckBox.setOnCheckedChangeListener { _, selected ->
            contactListInteractions?.let {
                if (selected) {
                    it.contactSelected(contact.contactId)
                } else {
                    it.contactDeselected(contact.contactId)
                }
            }

            holder.contactCheckBox.isChecked = contact.isSelected
        }
    }

    override fun getItemCount(): Int = contactList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.import_contacts_list_item, parent, false))
    }

    class ContactViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView by find(R.id.import_contacts_list_item_name)
        val contactCheckBox: CheckBox by find(R.id.import_contacts_checkbox)

    }

    interface ContactListInteraction {
        fun contactSelected(contactId: Long)
        fun contactDeselected(contactId: Long)
    }
}