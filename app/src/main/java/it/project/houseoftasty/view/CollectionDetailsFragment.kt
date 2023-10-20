package it.project.houseoftasty.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.adapter.PublicRecipeAdapter
import it.project.houseoftasty.databinding.FragmentCollectionDetailsBinding
import it.project.houseoftasty.viewModel.CollectionDetailsViewModel
import it.project.houseoftasty.viewModel.CollectionDetailsViewModelFactory
import kotlinx.coroutines.runBlocking


class CollectionDetailsFragment : Fragment() {

    // Parametri passati al Fragment dalla navigazione
    private val args: CollectionDetailsFragmentArgs by navArgs()

    private val collectionDetailsViewModel : CollectionDetailsViewModel by viewModels {
        CollectionDetailsViewModelFactory(args.collectionId)
    }
    private lateinit var binding: FragmentCollectionDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentCollectionDetailsBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = collectionDetailsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposta un osservatore su "actionBarTitle"
        collectionDetailsViewModel.actionBarTitle.observe(viewLifecycleOwner) {
            // Modifica il titolo della Action Bar
            (activity as MainActivity).setActionBarTitle(it)
        }

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        val recipeAdapter =
            if (collectionDetailsViewModel.getIsSaveCollection())
                PublicRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
            else
                PrivateRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recipeAdapter

        /* Imposta un clickListener sul F.A.B */
        val fab = binding.floatingActionButton
        fab.setOnClickListener {
            fab.setFabVisibility(false)
            fabOnClick()
        }

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        collectionDetailsViewModel.recipesLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                recipeAdapter.submitList(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Ricarica le ricette - utile in particolare dopo aver modificato una ricetta
        collectionDetailsViewModel.reinitialize()
    }

    /* Naviga verso CollectionFormFragment al click sul F.A.B */
    private fun fabOnClick() {
        navigateTo(CollectionDetailsFragmentDirections
            .actionCollectionDetailsFragmentToCollectionFormFragment(args.collectionId, null))
    }


    /* Naviga verso RecipeDetailFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        if (collectionDetailsViewModel.getIsSaveCollection())
            navigateTo(CollectionDetailsFragmentDirections
                .actionCollectionDetailsFragmentToRecipePostFragment(recipeId))
        else
            navigateTo(CollectionDetailsFragmentDirections
                .actionCollectionDetailsFragmentToRecipeDetailFragment(recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}