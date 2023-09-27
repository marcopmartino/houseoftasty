package it.project.houseoftasty.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.PrivateRecipeAdapter
import it.project.houseoftasty.adapter.PublicRecipeAdapter
import it.project.houseoftasty.databinding.FragmentSearchBinding
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.viewModel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private val searchViewModel : SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private var searchText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentSearchBinding.inflate(inflater, container, false)


        // Data Binding
        binding.viewModel = searchViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        searchView = binding.searchView
        recyclerView = binding.recyclerView

        searchView.setQuery("",false)
        searchView.clearFocus()

        searchView.queryHint = "Inserire il nome di una ricetta"

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        searchView.setQuery(searchText,true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setActionBarTitle("Esplora")

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val publicRecipeAdapter = PublicRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = publicRecipeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        searchViewModel.searchLiveData.observe(viewLifecycleOwner) {
            it?.let {
                /* Invia all'Adapter la lista di ricette da mostrare */
                publicRecipeAdapter.submitList(it)
            }
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchText = p0
                searchViewModel.searchRecipe(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d("query",p0.toString())
                searchText = p0
                searchViewModel.searchRecipe(p0)
                Log.d("query", searchViewModel.searchLiveData.value.toString())
                return false
            }

        })

    }
    override fun onStart() {
        super.onStart()

        // Ricarica le ricette - utile in particolare dopo aver modificato una ricetta
        searchViewModel.reinitialize()
    }

    /* Naviga verso RecipeDetailFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        navigateTo(SearchFragmentDirections.actionSearchFragmentToRecipePostFragment(recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}