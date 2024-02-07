package com.example.democontactlist.adapterdelegate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.democontactlist.databinding.ItemContactBinding
import com.example.democontactlist.items.ContactItem
import com.example.democontactlist.items.DisplayableItem
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ContactsDelegatesAdapter(
    onContactClickAction: ContactActionListener,
) : ListDelegationAdapter<List<DisplayableItem>>() {

    init {
        delegatesManager.addDelegate(ContactDelegate(onContactClickAction))
    }

    fun setData(items: List<DisplayableItem>) {
        val diffResult =
            DiffUtil.calculateDiff(DisplayableItemsDiffUtilCallback(this.items.orEmpty(), items))
        this.items = items
        diffResult.dispatchUpdatesTo(this)
    }
}

interface ContactActionListener {
    fun onContactSelect(item: ContactItem)
}

private class ContactDelegate(
    private val onContactClickAction: ContactActionListener,
) : AbsListItemAdapterDelegate<ContactItem, DisplayableItem, ContactDelegate.ViewHolder>(),
    View.OnClickListener {

    override fun isForViewType(
        item: DisplayableItem,
        items: List<DisplayableItem>,
        position: Int,
    ): Boolean {
        return item is ContactItem
    }

    override fun onClick(v: View) {
        val item: ContactItem = v.tag as ContactItem
        onContactClickAction.onContactSelect(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(item: ContactItem, holder: ViewHolder, payloads: List<Any>) {
        holder.itemView.tag = item
        with(holder.binding) {
            tvFullName.text = item.contact.firstName + " " + item.contact.lastName
            tvPhoneNumber.text = item.contact.phoneNumber
            if (item.checkable) cbCheck.visibility = View.VISIBLE
            else cbCheck.visibility = View.INVISIBLE
            cbCheck.isChecked = item.check
        }
    }

    class ViewHolder(
        val binding: ItemContactBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}