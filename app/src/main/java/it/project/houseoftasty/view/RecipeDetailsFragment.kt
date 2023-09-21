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
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.databinding.FragmentRecipeDetailsBinding
import it.project.houseoftasty.viewModel.RecipeDetailsViewModel
import it.project.houseoftasty.viewModel.RecipeDetailsViewModelFactory



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

        // Ottiene un riferimento al pulsante dei "Mi piace"
        val likeButton = binding.likeButton

        // Imposta un clickListener per il pulsante dei "Mi piace"
        likeButton.setOnClickListener {
            it.isClickable = false

            // Prima parte dell'animazione: rotazione di 90° in 250ms
            it.animate().apply {
                duration = 250
                rotationYBy(90f)
            }.withEndAction {

                // Codice da eseguire tra le due parti dell'animazione
                recipeDetailViewModel.toggleLikeButtonPressed()

                // Seconda parte dell'animazione: rotazione di 270° in 750ms
                it.animate().apply {
                    duration = 750
                    rotationYBy(270f)
                }.withEndAction {
                    it.isClickable = true
                }
            }.start()
        }

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