package it.project.houseoftasty.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.CollectionFormRecipeAdapter
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.databinding.FragmentCollectionFormBinding
import it.project.houseoftasty.utility.textToString
import it.project.houseoftasty.validation.EditTextValidator
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.Validator
import it.project.houseoftasty.viewModel.CollectionFormViewModel
import it.project.houseoftasty.viewModel.CollectionFormViewModelFactory

class CollectionFormFragment : Fragment() {

    private val collectionFormViewModel: CollectionFormViewModel by viewModels {
        // Factory class constructor
        CollectionFormViewModelFactory(args.collectionId, args.collectionName, args.recipeIdArray)
    }
    lateinit var binding: FragmentCollectionFormBinding
    private lateinit var recipeListAdapter: CollectionFormRecipeAdapter

    // Parametri passati al Fragment dalla navigazione
    private val args: CollectionFormFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating e View Binding
        binding = FragmentCollectionFormBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = collectionFormViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ottengo un riferimento alla MainActivity
        val mainActivity = activity as MainActivity

        // Modifico il titolo della Action Bar
        mainActivity.setActionBarTitle(
            if (collectionFormViewModel.isCollectionNew.value == true)
                "Nuova Raccolta" else "Modifica Raccolta"
        )

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        recipeListAdapter = CollectionFormRecipeAdapter { recipeId, elementPosition -> adapterOnClick(recipeId, elementPosition) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recipeListAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        collectionFormViewModel.recipeListLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                recipeListAdapter.submitList(it)
            }
        }

        /* Naviga verso CollectionAddRecipeFragment */
        binding.addRecipeButton.setOnClickListener {
            navigateTo(CollectionFormFragmentDirections.actionCollectionFormFragmentToCollectionAddRecipeFragment(
                args.collectionId,
                binding.dataName.textToString(),
                collectionFormViewModel.getRecipeIds().toTypedArray()
            ))
        }

        // Costruisce un gestore per la form
        collectionFormViewModel.generateFormManagerBuilder()
            .addEditTextValidators(EditTextValidator.Builder()
                .setInputView(binding.dataName)
                .setErrorView(binding.errorName)
                .addRules(ValidationRule.Required(), ValidationRule.MaxLength(20))
                .enableOnTextChangedValidation()
                .enableOnFocusLostValidation())
            .setSubmitButton(binding.buttonSubmitCollection)
            .setDeleteButton(binding.buttonDeleteCollection)
            .setDeleteConfirmationDialog(requireContext(),
                "Sei sicuro di voler eliminare la raccolta?")
            .build()

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        collectionFormViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", collectionFormViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "INSERTION" -> { navigateTo(CollectionFormFragmentDirections
                        .actionCollectionFormFragmentToNavCollections())
                    }
                    "UPDATE" -> { navigateTo(CollectionFormFragmentDirections
                        .actionCollectionFormFragmentToCollectionDetailsFragment(args.collectionId!!))
                    }
                    "DELETION" -> { navigateTo(CollectionFormFragmentDirections
                        .actionCollectionFormFragmentToNavCollections())
                    }
                    "NONE" -> { navigateTo(CollectionFormFragmentDirections
                        .actionCollectionFormFragmentToCollectionDetailsFragment(args.collectionId!!))
                    }
                    else -> { }
                }
            }
        }

    }

    // Funzione eseguita al click su un elemento della RecyclerView
    private fun adapterOnClick(recipeId: String, elementPosition: Int) {
        // Mostra un Dialog di conferma per la rimozione della ricetta dalla lista
        AlertDialog.Builder(context)
            .setTitle("Conferma rimozione")
            .setMessage("Sei sicuro di voler rimuovere la ricetta dalla raccolta?")
            .setCancelable(false)
            .setPositiveButton("Sì") { _, _ ->
                // Aggiorna RecyclerView e LiveData rimuovendo la ricetta
                recipeListAdapter.notifyItemRemoved(elementPosition)
                collectionFormViewModel.removeRecipeById(recipeId)
            }
            .setNegativeButton("No") { dialog, _ ->
                // Rimuove il Dialog
                dialog.dismiss()
            }
            .create().show()
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }


}