package com.example.democontactlist.items

import com.example.democontactlist.model.Contact

data class ContactItem(
    val contact: Contact,
    val checkable: Boolean,
    val check: Boolean,
) : DisplayableItem