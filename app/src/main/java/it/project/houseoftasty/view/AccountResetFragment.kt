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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.databinding.FragmentAccountResetBinding
import it.project.houseoftasty.databinding.FragmentCookbookBinding
import it.project.houseoftasty.viewModel.AccountResetViewModel
import it.project.houseoftasty.viewModel.CookbookViewModel
import kotlinx.coroutines.launch

class AccountResetFragment : Fragment() {

    private val accountResetViewModel : AccountResetViewModel by viewModels()
    private lateinit var binding: FragmentAccountResetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentAccountResetBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = accountResetViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Reset Account")

        val toast = Toast.makeText(context, "Reset eseguito con successo", Toast.LENGTH_LONG)
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Conferma reset account")
            .setMessage("Sei sicuro di voler eseguire il reset dell'account? L'operazione non potrà essere annullata.")
            .setCancelable(false)
            .setPositiveButton("Sì") { _, _ ->
                // Esegue la funzione di reset
                accountResetViewModel.resetAccount { toast.show() }
            }
            .setNegativeButton("No") { dialog, _ ->
                // Rimuove il Dialog
                dialog.dismiss()
            }
            .create()

        binding.accountResetButton.setOnClickListener {
            alertDialog.show()
        }

    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}