package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregmcgowan.fivesorganiser.core.ui.DiffUtilCallback
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.import_contacts_list_item.*

class ImportPlayersAdapter : RecyclerView.Adapter<ImportPlayersAdapter.ContactViewHolder>() {

    private var contactList: MutableList<ContactItemUiModel> = mutableListOf()

    var contactListInteractions: ContactListInteraction? = null

    fun setContacts(newContacts: List<ContactItemUiModel>) {
        val calculateDiff = DiffUtil.calculateDiff(
                DiffUtilCallback(contactList, newContacts) { c1, c2 -> c1.contactId == c2.contactId }
        )
        this.contactList.clear()
        this.contactList.addAll(newContacts)
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.import_contacts_list_item_name.text = contact.name
        holder.import_contacts_checkbox.setOnCheckedChangeListener { _, selected ->
            contactListInteractions?.let {
                if (selected) {
                    it.contactSelected(contact.contactId)
                } else {
                    it.contactDeselected(contact.contactId)
                }
            }
        }
        holder.import_contacts_checkbox.isChecked = contact.isSelected
    }

    override fun getItemCount(): Int = contactList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.import_contacts_list_item, parent, false))
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView
    }

    interface ContactListInteraction {
        fun contactSelected(contactId: Long)
        fun contactDeselected(contactId: Long)
    }
}