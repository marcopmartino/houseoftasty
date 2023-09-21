package it.project.houseoftasty.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.databinding.FragmentPopularBinding
import it.project.houseoftasty.databinding.FragmentRecentBinding
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.viewModel.RecentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RecentFragment : Fragment() {

    private val recentViewModel : RecentViewModel by viewModels()
    private lateinit var binding: FragmentRecentBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentRecentBinding.inflate(inflater, container, false)


        // Data Binding
        binding.viewModel = recentViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recyclerView = binding.recyclerView


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setActionBarTitle("Esplora")

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */

        val privateRecipeAdapter = PrivateRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = privateRecipeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        recentViewModel.recentLiveData.observe(viewLifecycleOwner) {
            it?.let {
                /* Invia all'Adapter la lista di ricette da mostrare */
                privateRecipeAdapter.submitList(it)
            }
        }

    }

    /* Naviga verso RecipeDetailFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        navigateTo(RecentFragmentDirections.actionRecentFragmentToRecipeDetailFragment(recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}