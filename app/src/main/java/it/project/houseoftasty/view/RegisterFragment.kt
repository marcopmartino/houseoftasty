package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.view.RegisterFragmentDirections
import it.project.houseoftasty.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as AccessActivity).setActionBarTitle("Registrazione")

        // Ottengo un riferimento al NavigationController
        val navController = requireView().findNavController()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")

        binding.buttonRegistrati.setOnClickListener{
            val username = binding.usernameRegister.text.toString()
            val email = binding.emailRegister.text.toString()
            val nome = binding.nomeRegister.text.toString()
            val cognome = binding.cognomeRegister.text.toString()
            val password = binding.passwordRegister.text.toString()
            val chkPassword = binding.confirmPasswordRegister.text.toString()

            if(username.isNotEmpty() && email.isNotEmpty() && nome.isNotEmpty() &&
                cognome.isNotEmpty() && password.isNotEmpty() && chkPassword.isNotEmpty()){
                if(isValidEmail(email)) {
                    if (password == chkPassword) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                createDbData(username, email, nome, cognome)
                                /*
                                val fragment = parentFragmentManager.beginTransaction()
                                fragment.replace(R.id.fragment_container, LoginFragment()).commit()

                                 */
                                navController.navigate(
                                    RegisterFragmentDirections.actionRegisterFragmentToMainActivity()
                                )
                            }.addOnFailureListener {
                                    exception: java.lang.Exception -> Toast.makeText(activity, exception.toString(), Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(activity, "Le password non corrispondono!",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(activity, "Inserisci una mail valida!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity, "Riempi tutti i campi", Toast.LENGTH_SHORT).show()
            }

        }

       binding.textBottomBar.setOnClickListener{
            /*
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, LoginFragment()).commit()

             */
            navController.navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
    }

    private fun isValidEmail(mail: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    private fun createDbData(username: String, email:String, nome:String, cognome:String ){
        try {
            val user = HashMap<String, Any>()
            user["username"] = username
            user["email"] = email
            user["nome"] = nome
            user["cognome"] = cognome
            firebaseDb.document(firebaseAuth.currentUser!!.uid).set(user).addOnSuccessListener {
                Toast.makeText(activity, "Account creato!!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                exception: java.lang.Exception -> Toast.makeText(activity, exception.toString(), Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

