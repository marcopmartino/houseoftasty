package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.adapter.PublicRecipeAdapter
import it.project.houseoftasty.adapter.PublicRecipeHomeAdapter
import it.project.houseoftasty.databinding.FragmentHomeBinding
import it.project.houseoftasty.databinding.FragmentRecentBinding
import it.project.houseoftasty.viewModel.HomeViewModel
import it.project.houseoftasty.viewModel.RecentViewModel

class HomeFragment : Fragment() {

    private val homeViewModel : HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflating e View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        // Data Binding
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recyclerView = binding.newestRecipes


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Home")

        val publicRecipeHomeAdapter = PublicRecipeHomeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = publicRecipeHomeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        homeViewModel.homeLiveData.observe(viewLifecycleOwner) {
            it?.let {
                /* Invia all'Adapter la lista di ricette da mostrare */
                publicRecipeHomeAdapter.submitList(it)
            }
        }
    }

    /* Naviga verso RecipeDetailFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        navigateTo(HomeFragmentDirections.actionHomeFragmentToRecipePostFragment(recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }
}