package it.project.houseoftasty.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentProfileFormBinding
import it.project.houseoftasty.validation.EditTextValidator
import it.project.houseoftasty.validation.ImageViewValidator
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.viewModel.ProfileFormViewModel
import it.project.houseoftasty.viewModel.ProfileFormViewModelFactory
import kotlinx.coroutines.*
import java.util.*

class ProfileFormFragment : Fragment() {

    private val profileFormViewModel: ProfileFormViewModel by viewModels {
        // Factory class constructor
        ProfileFormViewModelFactory()
    }

    lateinit var binding: FragmentProfileFormBinding


    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentProfileFormBinding.inflate(inflater, container, false)

        // Data Binding
        binding.profileData = profileFormViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = (activity as MainActivity)

        mainActivity.setActionBarTitle("Modifica Profilo")

        // Campi di input della form
        val dataUsernameView: EditText = binding.dataUsername
        val dataNomeView: EditText = binding.dataNome
        val dataCognomeView:EditText = binding.dataCognome
        val dataEmailView: EditText = binding.dataEmail
        val dataPswView: EditText = binding.dataPsw
        val dataNewPswView: EditText = binding.dataNewPsw
        val dataChkNewPswView: EditText = binding.dataChkPsw
        val dataImage: ImageView = binding.profileImage

        // Taglia l'immagine oltre i bordi (gli angoli dell'ImageView sono arrotondati)
        dataImage.clipToOutline = true

        // Costruisce un validatore per l'ImageView
        val imageViewValidator: ImageViewValidator =
            ImageViewValidator.Builder()
                .setInputView(dataImage)
                .setErrorView(binding.errorImage)
                .setSelectButton(binding.buttonEditImage)
                .setRemoveButton(binding.buttonRemoveImage)
                .setDefaultImage(R.drawable.img_peopleiconcol)
                .onSelectButtonClicked {
                    mainActivity.showPictureSelectionDialog()
                }.build()

        // Costruisce un validatore per la form
        profileFormViewModel.generateFormManagerBuilder()
            .addEditTextValidators(
                EditTextValidator.Builder()
                    .setInputView(dataUsernameView) // Username
                    .setErrorView(binding.errorUsername)
                    .addRules(ValidationRule.Required(),
                        ValidationRule.MinLength(4),
                        ValidationRule.MaxLength(20)),
                EditTextValidator.Builder()
                    .setInputView(dataNomeView) // Nome
                    .setErrorView(binding.errorNome)
                    .addRules(ValidationRule.Required(), ValidationRule.MaxLength(30)),
                EditTextValidator.Builder()
                    .setInputView(dataCognomeView) // Cognome
                    .setErrorView(binding.errorCognome)
                    .addRules(ValidationRule.Required(), ValidationRule.MaxLength(30)),
                EditTextValidator.Builder()
                    .setInputView(dataEmailView) // Email
                    .setErrorView(binding.errorEmail)
                    .addRules(ValidationRule.Required(), ValidationRule.Mail()),
                EditTextValidator.Builder()
                    .setInputView(dataPswView)
                    .setErrorView(binding.errorPsw)
                    .addRules(ValidationRule.Required())
                    .enableOnFocusLostValidation(),
                EditTextValidator.Builder()
                    .setInputView(dataNewPswView)
                    .setErrorView(binding.errorNewPsw)
                    .addRules(ValidationRule.Custom(
                        "La password deve essere lunga tra 8 e 24 caratteri") {
                        inputText -> if (inputText.isNullOrEmpty()) true
                            else ValidationRule.Length(8, 24).validate(inputText)
                    })
                    .enableOnFocusLostValidation(),
                EditTextValidator.Builder()
                    .setInputView(dataChkNewPswView)
                    .setErrorView(binding.errorChkPsw)
                    .addRules(ValidationRule.PasswordCheckField(dataNewPswView))
                    .enableOnFocusLostValidation())
            .addImageViewValidators(imageViewValidator)
            .setSubmitButton(binding.buttonSubmitProfile)
            .enableOnTextChangedValidation()
            .enableOnFocusLostValidation()
            .build()

        // Osservatore per il LiveData "isPasswordWrong"
        profileFormViewModel.isPasswordWrong.observe(viewLifecycleOwner) {
            if (it == true)
                binding.errorPsw.text = "Password corrente errata!"
        }

        // Osservatore per il LiveData del profilo
        profileFormViewModel.profileLiveData.observe(viewLifecycleOwner) {
            it.let {
                /* Se la ricetta non ha un'immagine, mostra l'immagine "carica_immagine" e
                * disabilita il pulsante di rimozione dell'immagine, altrimenti carica l'immagine
                * e abilita il pulsante.
                 */
                Log.d("DataInitialized", profileFormViewModel.profileLiveData.value.toString())
                Log.d("DataInitialized", profileFormViewModel.profileImageExists().toString())
                if (profileFormViewModel.profileImageExists()) {
                    imageViewValidator.enableRemoveButton()
                    imageViewValidator.loadImageFromReference(
                        requireContext(),
                        profileFormViewModel.profileLiveData.value!!.imageReference!!)
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

        // Funzione per navigare verso altri Fragment
        fun navigateTo(direction: NavDirections) {
            requireView().findNavController().navigate(direction)
        }

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        profileFormViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", profileFormViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "UPDATE" -> { navigateTo(ProfileFormFragmentDirections
                        .actionProfileFormFragmentToProfileFragment())
                    }
                    "NONE" -> { navigateTo(ProfileFormFragmentDirections
                        .actionProfileFormFragmentToProfileFragment())
                    }
                    else -> { }
                }
            }
        }
    }
}

