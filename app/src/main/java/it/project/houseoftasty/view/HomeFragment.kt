package it.project.houseoftasty.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.adapter.ProductAdapter
import it.project.houseoftasty.adapter.PublicRecipeAdapter
import it.project.houseoftasty.adapter.PublicRecipeHomeAdapter
import it.project.houseoftasty.databinding.FragmentHomeBinding
import it.project.houseoftasty.databinding.FragmentRecentBinding
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.viewModel.HomeViewModel
import it.project.houseoftasty.viewModel.RecentViewModel

class HomeFragment : Fragment() {

    private val homeViewModel : HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var infoText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflating e View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        // Data Binding
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recipesRecyclerView = binding.newestRecipes
        productsRecyclerView = binding.expireProduct
        infoText = binding.expireNoProduct


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Home")

        val publicRecipeHomeAdapter = PublicRecipeHomeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val productAdapter = ProductAdapter(requireContext(), resources) {product -> adapterOnClick(product)}
        val recipeLayoutManager = LinearLayoutManager(requireContext())
        val productLayoutManager = LinearLayoutManager(requireContext())
        recipeLayoutManager.orientation = LinearLayoutManager.VERTICAL
        productLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recipesRecyclerView.layoutManager = recipeLayoutManager
        recipesRecyclerView.adapter = publicRecipeHomeAdapter
        productsRecyclerView.layoutManager = productLayoutManager
        productsRecyclerView.adapter = productAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        homeViewModel.homeLiveData.observe(viewLifecycleOwner) {
            it?.let {
                /* Invia all'Adapter la lista di ricette da mostrare */
                publicRecipeHomeAdapter.submitList(it)
            }
        }

        homeViewModel.productLiveData.observe(viewLifecycleOwner){
            it?.let{
                productAdapter.submitList(it)
            }
        }
    }

    /* Naviga verso RecipePost al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        navigateTo(HomeFragmentDirections.actionHomeFragmentToRecipePostFragment(recipeId))
    }

    /* Naviga verso ProductForm al click su un elemento della RecyclerView */
    private fun adapterOnClick(product: Product){

        navigateTo(HomeFragmentDirections.actionHomeFragmentToProductFormFragment(product.id, true))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }
}