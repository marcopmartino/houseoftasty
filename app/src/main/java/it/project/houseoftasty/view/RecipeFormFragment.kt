package it.project.houseoftasty.view

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
import it.project.houseoftasty.databinding.FragmentRecipeFormBinding
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.Validator
import it.project.houseoftasty.viewModel.RecipeFormViewModel
import it.project.houseoftasty.viewModel.RecipeFormViewModelFactory

class RecipeFormFragment : Fragment() {

    private val recipeFormViewModel: RecipeFormViewModel by viewModels {
        // Factory class constructor
        RecipeFormViewModelFactory(args.recipeId)
    }
    lateinit var binding: FragmentRecipeFormBinding

    // Parametri passati al Fragment dalla navigazione
    private val args: RecipeFormFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating e View Binding
        binding = FragmentRecipeFormBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = recipeFormViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle(
            if (recipeFormViewModel.isRecipeNew.value == true)
                "Nuova Ricetta" else "Modifica Ricetta"
        )

        // Campi di input della form
        val dataTitleView = binding.dataTitle
        val dataIngredientsView = binding.dataIngredients
        val dataNumPeopleView = binding.dataNumPeople
        val dataPreparationView = binding.dataPreparation
        val dataPreparationTimeView = binding.dataPreparationTime
        val switchPublished = binding.switchPublished
        val switchPostPrivate = binding.switchPostPrivate

        // Costruisce un validatore per la form
        recipeFormViewModel.generateValidatorBuilder()
            .addValidators(
                Validator.Builder()
                    .setInputView(dataTitleView)
                    .setErrorView(binding.errorTitle)
                    .addRules(ValidationRule.MaxLength(50)),
                Validator.Builder()
                    .setInputView(dataIngredientsView)
                    .setErrorView(binding.errorIngredients)
                    .addRules(ValidationRule.MaxLength(1000)),
                Validator.Builder()
                    .setInputView(dataNumPeopleView)
                    .setErrorView(binding.errorNumPeople)
                    .addRules(ValidationRule.MinValue(1),
                        ValidationRule.MaxValue(50)),
                Validator.Builder()
                    .setInputView(dataPreparationView)
                    .setErrorView(binding.errorPreparation)
                    .addRules(ValidationRule.MaxLength(5000)),
                Validator.Builder()
                    .setInputView(dataPreparationTimeView)
                    .setErrorView(binding.errorPreparationTime)
                    .addRules(ValidationRule.MinValue(1),
                        ValidationRule.MaxValue(1440)))
            .addUnvalidatedFields(switchPublished, switchPostPrivate)
            .setSubmitButton(binding.buttonSubmitRecipe)
            .setDeleteButton(binding.buttonDeleteRecipe)
            .addCommonRules(ValidationRule.Required())
            .enableOnTextChangedValidation()
            .enableOnFocusLostValidation()
            .build()

        // Disabilita switchPostPrivate quando switchPublished è impostato a FALSE
        switchPublished.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchPostPrivate.isEnabled = true
            } else {
                switchPostPrivate.isEnabled = false
                switchPostPrivate.isChecked = false
            }
        }

        // Funzione per navigare verso altri Fragment
        fun navigateTo(direction: NavDirections) {
            requireView().findNavController().navigate(direction)
        }

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        recipeFormViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", recipeFormViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "INSERTION" -> { navigateTo(RecipeFormFragmentDirections
                        .actionRecipeFormFragmentToNavCookbook())
                    }
                    "UPDATE" -> { navigateTo(RecipeFormFragmentDirections
                        .actionRecipeFormFragmentToRecipeDetailFragment(args.recipeId!!))
                    }
                    "DELETION" -> { navigateTo(RecipeFormFragmentDirections
                        .actionRecipeFormFragmentToNavCookbook())
                    }
                    "NONE" -> {
                        (activity as MainActivity).supportFragmentManager.popBackStack()
                    }
                    else -> { }
                }
            }
        }

    }

}