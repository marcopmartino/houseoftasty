package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.databinding.FragmentRecipeDetailsBinding
import it.project.houseoftasty.viewModel.RecipeDetailsViewModel
import it.project.houseoftasty.viewModel.RecipeDetailsViewModelFactory
import kotlinx.coroutines.runBlocking


class RecipeDetailsFragment : Fragment() {


    private val recipeDetailViewModel : RecipeDetailsViewModel by viewModels {
        // Factory class constructor
        RecipeDetailsViewModelFactory(args.recipeId)
    }

    lateinit var binding: FragmentRecipeDetailsBinding

    // Parametri passati al Fragment dalla navigazione
    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating e View Binding
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = recipeDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Dettagli Ricetta")

        /* Imposta un clickListener sul F.A.B */
        val fab = binding.floatingActionButton
        fab.setOnClickListener {
            fab.setFabVisibility(false)
            fabOnClick()
        }

    }

    /* Naviga verso RecipeFormFragment al click sul F.A.B */
    private fun fabOnClick() {
        val action : NavDirections = RecipeDetailsFragmentDirections
                .actionRecipeDetailFragmentToRecipeFormFragment(args.recipeId)
        requireView().findNavController().navigate(action)
    }


}