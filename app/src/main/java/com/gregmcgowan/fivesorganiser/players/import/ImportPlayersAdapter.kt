package com.gregmcgowan.fivesorganiser.players.import

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.R
import com.gregmcgowan.fivesorganiser.find
import java.util.*

class ImportPlayersAdapter : RecyclerView.Adapter<ImportPlayersAdapter.ContactViewHolder>() {

    var contactList: List<Contact> = ArrayList()

    var contactListener: ImportContactsContract.ContactItemListener? = null

    fun setContacts(contacts: List<Contact>) {
        this.contactList = contacts
        notifyDataSetChanged()
    }

    fun setListener(contactListener: ImportContactsContract.ContactItemListener) {
        this.contactListener = contactListener
    }

    override fun onBindViewHolder(holder: ContactViewHolder?, position: Int) {
        val contact = contactList[position]

        holder?.let {
            holder.contactName.text = contact.name
            holder.contactCheckBox.setOnCheckedChangeListener { _, selected ->
                if (selected) {
                    contactListener?.contactSelected(contact.contactId)
                } else {
                    contactListener?.contactDeselected(contact.contactId)
                }
            }
            holder.contactCheckBox.isChecked = contactListener
                    ?.getSelectedItems()
                    ?.contains(contact.contactId) as Boolean
        }
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
        val contactName: TextView by find<TextView>(R.id.import_contacts_list_item_name)
        val contactCheckBox: CheckBox by find<CheckBox>(R.id.import_contacts_checkbox)
    }
}