package it.project.houseoftasty.view

import android.graphics.Bitmap
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
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentRecipeFormBinding
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.EditTextValidator
import it.project.houseoftasty.validation.ImageViewValidator
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

        // Ottengo un riferimento alla MainActivity
        val mainActivity = activity as MainActivity

        // Modifico il titolo della Action Bar
        mainActivity.setActionBarTitle(
            if (recipeFormViewModel.isRecipeNew.value == true)
                "Nuova Ricetta" else "Modifica Ricetta"
        )

        // Campi di input della form
        val dataTitleView = binding.dataTitle
        val dataIngredientsView = binding.dataIngredients
        val dataNumPeopleView = binding.dataNumPeople
        val dataPreparationView = binding.dataPreparation
        val dataPreparationTimeView = binding.dataPreparationTime
        val dataImage = binding.dataImage
        val switchPublished = binding.switchPublished
        val switchPostPrivate = binding.switchPostPrivate

        // Taglia l'immagine oltre i bordi (gli angoli dell'ImageView sono arrotondati)
        dataImage.clipToOutline = true

        // Costruisce un validatore per l'ImageView
        val imageViewValidator: ImageViewValidator =
            ImageViewValidator.Builder()
                .setInputView(dataImage)
                .setErrorView(binding.errorImage)
                .setRemoveButton(binding.buttonRemoveImage)
                .setDefaultImage(R.drawable.carica_immagine)
                .onSelectButtonClicked {
                    mainActivity.showPictureSelectionDialog()
                }.build()

        // Costruisce un gestore per la form al termine dell'inizializzazione
        recipeFormViewModel.generateFormManagerBuilder(customHasDataChangedFunction = false)
            .addEditTextValidators(
                EditTextValidator.Builder()
                    .setInputView(dataTitleView)
                    .setErrorView(binding.errorTitle)
                    .addRules(ValidationRule.MaxLength(50)),
                EditTextValidator.Builder()
                    .setInputView(dataIngredientsView)
                    .setErrorView(binding.errorIngredients)
                    .addRules(ValidationRule.MaxLength(1000)),
                EditTextValidator.Builder()
                    .setInputView(dataNumPeopleView)
                    .setErrorView(binding.errorNumPeople)
                    .addRules(ValidationRule.MinValue(1),
                        ValidationRule.MaxValue(50)),
                EditTextValidator.Builder()
                    .setInputView(dataPreparationView)
                    .setErrorView(binding.errorPreparation)
                    .addRules(ValidationRule.MaxLength(5000)),
                EditTextValidator.Builder()
                    .setInputView(dataPreparationTimeView)
                    .setErrorView(binding.errorPreparationTime)
                    .addRules(ValidationRule.MinValue(1),
                        ValidationRule.MaxValue(1440)))
            .addImageViewValidators(imageViewValidator)
            .addUnvalidatedFields(switchPublished, switchPostPrivate)
            .setSubmitButton(binding.buttonSubmitRecipe)
            .setDeleteButton(binding.buttonDeleteRecipe)
            .setDeleteConfirmationDialog(
                requireContext(),
                "Sei sicuro di voler eliminare la ricetta?")
            .addCommonRules(ValidationRule.Required())
            .enableOnTextChangedValidation()
            .enableOnFocusLostValidation()
            .build()

        // Osservatore per il LiveData della ricetta
        recipeFormViewModel.recipeLiveData.observe(viewLifecycleOwner) {
            it.let {
                /* Se la ricetta non ha un'immagine, mostra l'immagine "carica_immagine" e
                * disabilita il pulsante di rimozione dell'immagine, altrimenti carica l'immagine
                * e abilita il pulsante.
                 */
                Log.d("DataInitialized", recipeFormViewModel.recipeLiveData.value.toString())
                Log.d("DataInitialized", recipeFormViewModel.recipeImageExists().toString())
                if (recipeFormViewModel.recipeImageExists()) {
                    imageViewValidator.enableRemoveButton()
                    imageViewValidator.loadImageFromReference(
                        requireContext(),
                        recipeFormViewModel.recipeLiveData.value!!.imageReference!!)
                } else {
                    imageViewValidator.disableRemoveButton()
                    imageViewValidator.loadDefaultImage()
                }
            }
        }

        // Gestisco i dati ritornati da un'activity fotocamera
        mainActivity.onCameraActivityResult = {
            imageViewValidator.loadBitmap(it?.extras?.get("data") as Bitmap)
            imageViewValidator.onSelectSuccess()
        }

        // Gestisco i dati ritornati da un'activity galleria
        mainActivity.onGalleryActivityResult = {
            imageViewValidator.loadFromUri(it?.data)
            imageViewValidator.onSelectSuccess()
        }

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
                    "NONE" -> { navigateTo(RecipeFormFragmentDirections
                        .actionRecipeFormFragmentToRecipeDetailFragment(args.recipeId!!))
                    }
                    else -> { }
                }
            }
        }

    }

}