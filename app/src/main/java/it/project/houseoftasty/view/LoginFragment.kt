package it.project.houseoftasty.view


import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.view.LoginFragmentDirections
import it.project.houseoftasty.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb:  DocumentReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as AccessActivity).setActionBarTitle("Login")

        // Ottengo un riferimento al NavigationController
        val navController = requireView().findNavController()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonAccedi.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseDb = FirebaseFirestore.getInstance().collection("users").document(email)
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        /*
                        val navView = activity?.findViewById<NavigationView>(R.id.navView)
                        navView!!.menu.clear()
                        navView.inflateMenu(R.menu.menu_authenticated)
                        (activity as AppCompatActivity).supportActionBar?.title ="Profilo"

                        val fragment = parentFragmentManager.beginTransaction()
                        fragment.replace(R.id.fragment_container, ProfileFragment()).commit()

                         */
                        navController.navigate(
                            LoginFragmentDirections.actionLoginFragmentToMainActivity()
                        )
                    }else{
                        Toast.makeText(activity, "Username e/o password errati!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(activity, "Campi vuoti non ammessi!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textBottomBar.setOnClickListener {
            /*
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, RegisterFragment()).commit()

             */
            navController.navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }
    }
}

