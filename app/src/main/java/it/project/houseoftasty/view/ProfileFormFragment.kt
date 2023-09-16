package it.project.houseoftasty.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.databinding.FragmentProfileFormBinding
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.Validator
import it.project.houseoftasty.viewModel.ProfileFormViewModel
import it.project.houseoftasty.viewModel.ProfileFormViewModelFactory
import kotlinx.coroutines.*
import java.util.*

class ProfileFormFragment : Fragment() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val profileFormViewModel: ProfileFormViewModel by viewModels {
        // Factory class constructor
        ProfileFormViewModelFactory(firebaseAuth.currentUser!!.uid)
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

        (activity as MainActivity).setActionBarTitle("Modifica profilo")

        val retriveString: (str: String?) -> Boolean = {
            it == binding.dataNewPsw.text.toString()
        }

        // Campi di input della form
        val dataUsernameView: EditText = binding.dataUsername
        val dataNomeView: EditText = binding.dataNome
        val dataCognomeView:EditText = binding.dataCognome
        val dataEmailView: EditText = binding.dataEmail
        val dataPswView: EditText = binding.dataPsw
        val dataNewPswView: EditText = binding.dataNewPsw
        val dataChkNewPswView: EditText = binding.dataChkPsw

        // Costruisce un validatore per la form
        profileFormViewModel.generateValidatorBuilder()
            .addValidators(
                Validator.Builder()
                    .setInputView(dataUsernameView) //Username
                    .setErrorView(binding.errorUsername)
                    .addRules(ValidationRule.MaxLength(20), ValidationRule.Required()),
                Validator.Builder()
                    .setInputView(dataNomeView) //Nome
                    .setErrorView(binding.errorNome)
                    .addRules(ValidationRule.MaxLength(50), ValidationRule.Required()),
                Validator.Builder()
                    .setInputView(dataCognomeView) //Cognome
                    .setErrorView(binding.errorCognome)
                    .addRules(ValidationRule.MaxLength(50), ValidationRule.Required()),
                Validator.Builder()
                    .setInputView(dataEmailView) //Email
                    .setErrorView(binding.errorEmail)
                    .addRules(ValidationRule.isMail(), ValidationRule.Required()),
                Validator.Builder()
                    .setInputView(dataPswView)
                    .setErrorView(binding.errorPsw)
                    .addRules(ValidationRule.isCurrentPsw(null, firebaseAuth), ValidationRule.Required()),
                Validator.Builder()
                    .setInputView(dataChkNewPswView)
                    .setErrorView(binding.errorChkPsw)
                    .addRules(ValidationRule.isEqualString(null, retriveString))
            )
            .addUnvalidatedFields(dataNewPswView)
            .setSubmitButton(binding.buttonSubmitProfile)
            .enableOnFocusLostValidation()
            .build()

        // Funzione per navigare verso altri Fragment
        fun navigateTo(direction: NavDirections) {
            requireView().findNavController().navigate(direction)
        }

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        profileFormViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", profileFormViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "INSERTION" -> { navigateTo(ProfileFormFragmentDirections
                        .actionProfileFormFragmentToProfileFragment())
                    }
                    "UPDATE" -> { navigateTo(ProfileFormFragmentDirections
                        .actionProfileFormFragmentToProfileFragment())
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

