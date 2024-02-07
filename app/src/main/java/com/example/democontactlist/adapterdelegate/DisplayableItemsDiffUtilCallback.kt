package com.example.democontactlist.adapterdelegate

import androidx.recyclerview.widget.DiffUtil
import com.example.democontactlist.items.ContactItem
import com.example.democontactlist.items.DisplayableItem

class DisplayableItemsDiffUtilCallback(
    private val oldItems: List<DisplayableItem>,
    private val newItems: List<DisplayableItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return when {
            oldItem is ContactItem && newItem is ContactItem &&
                    oldItem.contact.id == newItem.contact.id -> true

            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem == newItem
    }
}