package com.example.democontactlist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.example.democontactlist.databinding.DialogfragmentInputBinding
import com.example.democontactlist.model.Contact

typealias InputDialogListener = (requestKey: String, contact: Contact) -> Unit

class InputDialogFragment : DialogFragment() {

    private val currentContact: Contact
        get() = requireArguments().parcelable(ARG_CONTACT)!!
    private val requestKey: String
        get() = requireArguments().getString(ARG_REQUEST_KEY)!!

    @SuppressLint("DialogFragmentCallbacksDetector", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = DialogfragmentInputBinding.inflate(layoutInflater)
        var contact = Contact(0, "", "", "")
        val action: String
        val dialogBuilder = AlertDialog.Builder(requireContext())
        when (requestKey) {
            "KEY_ADD_REQUEST" -> {
                action = "Добавить"
                showKeyboard(dialogBinding.ietFirstName)
            }

            "KEY_EDIT_REQUEST" -> {
                action = "Изменить"
                contact = currentContact
                dialogBuilder.setNeutralButton(
                    "Удалить"
                ) { dialog, _ ->
                    parentFragmentManager.setFragmentResult(
                        "KEY_DELETE_REQUEST", bundleOf(
                            KEY_CONTACT_RESPONSE to contact
                        )
                    )
                    dialog.dismiss()
                }
            }

            else -> action = ""
        }

        dialogBinding.tvContactID.text = "id:" + contact.id.toString()
        dialogBinding.ietFirstName.setText(contact.firstName)
        dialogBinding.ietLastName.setText(contact.lastName)
        dialogBinding.ietPhoneNumber.setText(contact.phoneNumber)

        val dialog = dialogBuilder.setTitle("$action контакт").setView(dialogBinding.root)
            .setPositiveButton(action, null).setNegativeButton("Отмена", null).create()

        dialog.setOnShowListener {
            dialogBinding.ietFirstName.requestFocus()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val firstName = dialogBinding.ietFirstName.text.toString()
                val lastName = dialogBinding.ietLastName.text.toString()
                val phoneNumber = dialogBinding.ietPhoneNumber.text.toString()

                if (firstName.isBlank() && lastName.isBlank() && phoneNumber.isBlank()) {
                    if (firstName.isBlank()) dialogBinding.ietFirstName.error = "Введите данные"
                    if (lastName.isBlank()) dialogBinding.ietLastName.error = "Введите данные"
                    if (phoneNumber.isBlank()) dialogBinding.ietPhoneNumber.error = "Введите данные"
                    return@setOnClickListener
                }
                contact = Contact(
                    contact.id,
                    dialogBinding.ietFirstName.text.toString(),
                    dialogBinding.ietLastName.text.toString(),
                    dialogBinding.ietPhoneNumber.text.toString()
                )

                parentFragmentManager.setFragmentResult(
                    requestKey, bundleOf(
                        KEY_CONTACT_RESPONSE to contact
                    )
                )
                dismiss()
            }
        }
        dialog.setOnDismissListener { hideKeyboard(dialogBinding.ietFirstName) }
        return dialog
    }

    private fun showKeyboard(view: View) {
        view.post {
            getInputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard(view: View) {
        getInputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getInputMethodManager(view: View): InputMethodManager {
        val context = view.context
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    companion object {
        @JvmStatic
        private val TAG = InputDialogFragment::class.java.simpleName

        @JvmStatic
        private val KEY_CONTACT_RESPONSE = "KEY_CONTACT_RESPONSE"

        @JvmStatic
        private val ARG_CONTACT = "ARG_CONTACT"

        @JvmStatic
        private val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"

        fun showContact(manager: FragmentManager, contact: Contact, requestKey: String) {
            val dialogFragment = InputDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_CONTACT to contact, ARG_REQUEST_KEY to requestKey
            )
            dialogFragment.show(manager, TAG)
        }

        fun createContact(manager: FragmentManager, requestKey: String) {
            val dialogFragment = InputDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_REQUEST_KEY to requestKey
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            requestKey: String,
            listener: InputDialogListener,
        ) {
            manager.setFragmentResultListener(
                requestKey, lifecycleOwner
            ) { key, result ->
                listener.invoke(key, result.parcelable(KEY_CONTACT_RESPONSE)!!)
            }
        }
    }
}