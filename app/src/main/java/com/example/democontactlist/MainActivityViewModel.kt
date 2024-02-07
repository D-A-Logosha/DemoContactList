package com.example.democontactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.democontactlist.items.ContactItem
import com.example.democontactlist.items.DisplayableItem
import com.example.democontactlist.model.Contact
import com.example.democontactlist.model.ContactsListener
import com.example.democontactlist.model.ContactsRepository
import java.util.Collections

class MainActivityViewModel : ViewModel() {

    private val contactsRepository = ContactsRepository()

    private val _contactsResult = MutableLiveData<List<DisplayableItem>>()
    val contactsResult: LiveData<List<DisplayableItem>> = _contactsResult

    private val _deleteMode = MutableLiveData<Boolean>()
    val deleteMode: LiveData<Boolean> = _deleteMode

    private var checkContacts = mutableListOf<Contact>()

    private var contacts: List<Contact> = emptyList()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val _actionShowChangeDialog = MutableLiveData<Event<Contact>>()
    val actionShowChangeDialog: LiveData<Event<Contact>> = _actionShowChangeDialog

    private val _actionScrollToPosition = MutableLiveData<Event<Int>>()
    val actionScrollToPosition: LiveData<Event<Int>> = _actionScrollToPosition

    private val contactsListener: ContactsListener = {
        contacts = it
    }

    init {
        contactsRepository.addListener(contactsListener)
    }

    override fun onCleared() {
        super.onCleared()
        contactsRepository.removeListener(contactsListener)
    }

    fun selectContact(contact: Contact) {
        if (deleteMode.value == true) {
            if (checkContacts.contains(contact)) checkContacts.remove(contact)
            else checkContacts.add(contact)
            notifyUpdates()
        } else {
            showChangeContactDialog(contact)
        }
    }

    fun addContact(contact: Contact) {
        contactsRepository.addContact(contact)
        scrollToPosition(contactsRepository.getSize() - 1)
    }

    fun editContact(contact: Contact) {
        contactsRepository.editContact(contact)
    }

    fun moveContact(oldPosition: Int, newPosition: Int) {
        val movedList = _contactsResult.value?.toMutableList() ?: return
        Collections.swap(movedList, oldPosition, newPosition)
        _contactsResult.value = movedList
    }

    fun deleteContact(contact: Contact) {
        contactsRepository.deleteContact(contact)
    }

    fun deleteContacts() {
        checkContacts.forEach {
            contactsRepository.deleteContact(it)
        }
    }

    fun setDeleteMode(deleteMode: Boolean) {
        if (deleteMode) {
            checkContacts = mutableListOf()
        }
        _deleteMode.value = deleteMode
        notifyUpdates()
    }

    private fun showChangeContactDialog(contact: Contact) {
        _actionShowChangeDialog.value = Event(contact)
    }

    private fun scrollToPosition(position: Int) {
        _actionScrollToPosition.value = Event(position)
    }

    private fun notifyUpdates() {
        _contactsResult.postValue(contacts.map { contact ->
            ContactItem(
                contact, _deleteMode.value ?: false, checkContacts.contains(contact)
            )
        })
    }
}

class Event<T>(private val value: T) {
    private var handled: Boolean = false
    fun getValue(): T? {
        if (handled) return null
        handled = true
        return value
    }
}