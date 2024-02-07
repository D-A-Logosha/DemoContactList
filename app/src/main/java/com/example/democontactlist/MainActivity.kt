package com.example.democontactlist

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.democontactlist.adapterdelegate.ContactActionListener
import com.example.democontactlist.adapterdelegate.ContactsDelegatesAdapter
import com.example.democontactlist.databinding.ActivityMainBinding
import com.example.democontactlist.items.ContactItem
import com.example.democontactlist.items.DisplayableItem
import com.example.democontactlist.model.Contact

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var contactsList: RecyclerView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        itemTouchHelperInit()
        recyclerViewAdapterInit()
        setObserve()
        seListener()
    }

    private fun ContactsDelegatesAdapter.showData(data: List<DisplayableItem>) {
        setData(data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.setDeleteMode(true)
        return true
    }

    private fun itemTouchHelperInit() {

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val oldPosition = viewHolder.absoluteAdapterPosition
                val newPosition = target.absoluteAdapterPosition
                viewModel.moveContact(oldPosition, newPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun recyclerViewAdapterInit() {
        contactsList = binding.recyclerView
        contactsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ContactsDelegatesAdapter(object : ContactActionListener {
                override fun onContactSelect(item: ContactItem) {
                    viewModel.selectContact(item.contact)
                }
            })
        }
    }

    private fun setObserve() {
        viewModel.contactsResult.observe(this) {
            (contactsList.adapter as ContactsDelegatesAdapter).apply { showData(it) }
        }

        viewModel.deleteMode.observe(this) {
            if (it) {
                binding.toolbar.menu.findItem(R.id.i_setDeleteMode).setVisible(false)
                binding.fabAdd.visibility = View.INVISIBLE
                binding.fabDeleteOk.visibility = View.VISIBLE
                binding.fabDeleteCansel.visibility = View.VISIBLE
            } else {
                binding.toolbar.menu.findItem(R.id.i_setDeleteMode).setVisible(true)
                binding.fabAdd.visibility = View.VISIBLE
                binding.fabDeleteOk.visibility = View.INVISIBLE
                binding.fabDeleteCansel.visibility = View.INVISIBLE
            }
        }

        viewModel.actionShowChangeDialog.observe(this) {
            it.getValue()?.let { contact -> showChangeContactDialog(contact) }
        }

        viewModel.actionScrollToPosition.observe(this) {
            it.getValue()?.let { position -> binding.recyclerView.scrollToPosition(position) }
        }
    }

    private fun seListener() {
        binding.fabAdd.setOnClickListener {
            showCreateContactDialog()
        }

        binding.fabDeleteCansel.setOnClickListener {
            viewModel.setDeleteMode(false)
        }

        binding.fabDeleteOk.setOnClickListener {
            viewModel.deleteContacts()
            viewModel.setDeleteMode(false)
        }

        val listener: InputDialogListener = { requestKey, contact ->
            when (requestKey) {
                KEY_ADD_REQUEST -> viewModel.addContact(contact)
                KEY_EDIT_REQUEST -> viewModel.editContact(contact)
                KEY_DELETE_REQUEST -> viewModel.deleteContact(contact)
            }
        }
        InputDialogFragment.setupListener(
            supportFragmentManager, this, KEY_ADD_REQUEST, listener
        )
        InputDialogFragment.setupListener(
            supportFragmentManager, this, KEY_EDIT_REQUEST, listener
        )
        InputDialogFragment.setupListener(
            supportFragmentManager, this, KEY_DELETE_REQUEST, listener
        )
    }

    private fun showChangeContactDialog(contact: Contact) {
        InputDialogFragment.showContact(supportFragmentManager, contact, KEY_EDIT_REQUEST)
    }

    private fun showCreateContactDialog() {
        InputDialogFragment.createContact(supportFragmentManager, KEY_ADD_REQUEST)
    }

    companion object {
        @JvmStatic
        private val KEY_ADD_REQUEST = "KEY_ADD_REQUEST"

        @JvmStatic
        private val KEY_EDIT_REQUEST = "KEY_EDIT_REQUEST"

        @JvmStatic
        private val KEY_DELETE_REQUEST = "KEY_DELETE_REQUEST"
    }
}