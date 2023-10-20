package it.project.houseoftasty.view

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentAccountDeleteBinding
import it.project.houseoftasty.validation.EditTextValidator
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.viewModel.AccountDeleteViewModel
import kotlinx.coroutines.launch

class AccountDeleteFragment : Fragment() {

    private val accountDeleteViewModel : AccountDeleteViewModel by viewModels()
    private lateinit var binding: FragmentAccountDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentAccountDeleteBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = accountDeleteViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Elimina Account")

        // Ottengo i riferimento all'EditText per la password e alla TextView per mostrare errori
        val passwordEditText = binding.accountDeletePasswordEditText
        val errorTextView = binding.accountDeletePasswordErrorTextView

        // Osservatore per il LiveData "isPasswordWrong"
        accountDeleteViewModel.isPasswordWrong.observe(viewLifecycleOwner) {
            if (it == true)
                errorTextView.text = "Password errata"
        }

        // Validatore per l'EditText con la password
        val passwordValidator = EditTextValidator.Builder()
            .setInputView(passwordEditText)
            .setErrorView(errorTextView)
            .addRules(ValidationRule.Required("Inserisci la password"))
            .build()

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Conferma eliminazione account")
            .setMessage("Sei sicuro di voler eliminare l'account? L'operazione non potrà essere annullata.")
            .setCancelable(false)
            .setPositiveButton("Sì") { _, _ ->
                // Esegue la funzione di reset
                lifecycleScope.launch {
                    accountDeleteViewModel.deleteAccount(passwordValidator.getInputData().toString()) {
                        navigateTo(AccountDeleteFragmentDirections.actionAccountDeleteFragmentToNavLogout())
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                // Rimuove il Dialog
                dialog.dismiss()
            }
            .create()

        binding.accountDeleteButton.setOnClickListener {
            if (passwordValidator.validate())
                alertDialog.show()
        }

    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}