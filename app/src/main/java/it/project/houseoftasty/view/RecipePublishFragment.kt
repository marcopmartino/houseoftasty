package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.databinding.FragmentRecipePublishBinding
import it.project.houseoftasty.viewModel.RecipePublishViewModel
import kotlinx.coroutines.launch

class RecipePublishFragment : Fragment() {

    private val recipePublishViewModel : RecipePublishViewModel by viewModels()
    private lateinit var binding: FragmentRecipePublishBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentRecipePublishBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = recipePublishViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifica il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Pubblica Ricetta")

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        val privateRecipeAdapter = PrivateRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = privateRecipeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        recipePublishViewModel.recipesLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                privateRecipeAdapter.submitList(it)
            }
        }

        /* Imposta un osservatore per navigare al termine della pubblicazione della ricetta. */
        recipePublishViewModel.publicationCompleted.observe(viewLifecycleOwner) {
            it?.let {
                if (it)
                    navigateTo(RecipePublishFragmentDirections.actionRecipePublishFragmentToNavProfile())
            }
        }

    }

    /* Naviga verso PublicProfileFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        recipePublishViewModel.publishRecipe(recipeId)
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}