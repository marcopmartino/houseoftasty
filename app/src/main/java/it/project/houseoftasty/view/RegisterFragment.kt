package it.project.houseoftasty.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentProfileFormBinding
import it.project.houseoftasty.view.RegisterFragmentDirections
import it.project.houseoftasty.databinding.FragmentRegisterBinding
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.Validator
import it.project.houseoftasty.viewModel.ProfileFormViewModel
import it.project.houseoftasty.viewModel.ProfileFormViewModelFactory
import it.project.houseoftasty.viewModel.RegisterViewModel
import it.project.houseoftasty.viewModel.RegisterViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels{
        RegisterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        // Inflating e View Binding
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Data Binding
        binding.registerData = registerViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as AccessActivity).setActionBarTitle("Registrazione")

        // Ottengo un riferimento al NavigationController
        val navController = requireView().findNavController()

        val retriveString: (str: String?) -> Boolean = {
            it == binding.passwordRegister.text.toString()
        }

        // Campi di input della form
        val usernameView: EditText =  binding.usernameRegister
        val nomeView: EditText = binding.nomeRegister
        val cognomeView: EditText = binding.cognomeRegister
        val emailView: EditText = binding.emailRegister
        val pswView: EditText = binding.passwordRegister
        val chkPswView: EditText = binding.confirmPasswordRegister

        // Costruisce un validatore per la form
        registerViewModel.generateValidatorBuilder()
            .addValidators(
                Validator.Builder()
                    .setInputView(usernameView) //Username
                    .setErrorView(binding.errorUsername)
                    .addRules(ValidationRule.MaxLength(20)),
                Validator.Builder()
                    .setInputView(nomeView) //Nome
                    .setErrorView(binding.errorNome)
                    .addRules(ValidationRule.MaxLength(50)),
                Validator.Builder()
                    .setInputView(cognomeView) //Cognome
                    .setErrorView(binding.errorCognome)
                    .addRules(ValidationRule.MaxLength(50)),
                Validator.Builder()
                    .setInputView(emailView) //Email
                    .setErrorView(binding.errorEmail)
                    .addRules(ValidationRule.isMail()),
                Validator.Builder()
                    .setInputView(pswView) //Password
                    .setErrorView(binding.errorPsw),
                Validator.Builder()
                    .setInputView(chkPswView) //Conferma Password
                    .setErrorView(binding.errorChkPsw)
                    .addRules(ValidationRule.isEqualString(null, retriveString))
            )
            .setSubmitButton(binding.buttonSubmitRegister)
            .addCommonRules(ValidationRule.Required())
            .enableOnFocusLostValidation()
            .build()


       binding.textBottomBar.setOnClickListener{
            navController.navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }

        // Funzione per navigare verso altri Fragment
        fun navigateTo(direction: NavDirections) {
            requireView().findNavController().navigate(direction)
        }

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        registerViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", registerViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "INSERTION" -> { navigateTo(RegisterFragmentDirections
                        .actionRegisterFragmentToProfileFragment())
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

