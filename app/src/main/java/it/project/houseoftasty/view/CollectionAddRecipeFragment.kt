package it.project.houseoftasty.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.databinding.FragmentCollectionAddRecipeBinding
import it.project.houseoftasty.databinding.FragmentCollectionDetailsBinding
import it.project.houseoftasty.viewModel.CollectionAddRecipeViewModel
import it.project.houseoftasty.viewModel.CollectionAddRecipeViewModelFactory
import it.project.houseoftasty.viewModel.CollectionDetailsViewModel
import it.project.houseoftasty.viewModel.CollectionDetailsViewModelFactory

class CollectionAddRecipeFragment : Fragment() {

    // Parametri passati al Fragment dalla navigazione
    private val args: CollectionAddRecipeFragmentArgs by navArgs()

    private val collectionAddRecipeViewModel : CollectionAddRecipeViewModel by viewModels {
        CollectionAddRecipeViewModelFactory(args.recipeIdArray)
    }
    private lateinit var binding: FragmentCollectionAddRecipeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentCollectionAddRecipeBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = collectionAddRecipeViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifica il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Aggiungi Ricetta")

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        val privateRecipeAdapter = PrivateRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = privateRecipeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        collectionAddRecipeViewModel.recipesLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                privateRecipeAdapter.submitList(it)
            }
        }
    }

    /* Naviga verso CollectionFormFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        // Aggiunge l'id della ricetta selezionata alla lista di id
        collectionAddRecipeViewModel.addRecipeId(recipeId)

        // Naviga passando la lista aggiornata al fragment della form
        navigateTo(CollectionAddRecipeFragmentDirections
            .actionCollectionAddRecipeFragmentToCollectionFormFragment(
                args.collectionId,
                args.collectionName,
                collectionAddRecipeViewModel.getRecipeIds()))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}