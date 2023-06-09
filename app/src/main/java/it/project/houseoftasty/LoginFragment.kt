package it.project.houseoftasty


import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb:  DocumentReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()


        view.findViewById<Button>(R.id.button_accedi).setOnClickListener{
            val email = view.findViewById<EditText>(R.id.email_login).text.toString()
            val password = view.findViewById<EditText>(R.id.password_login).text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseDb = FirebaseFirestore.getInstance().collection("users").document(email)
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val navView = activity?.findViewById<NavigationView>(R.id.navView)
                        navView!!.menu.clear()
                        navView.inflateMenu(R.menu.after_login)
                        (activity as AppCompatActivity).supportActionBar?.title ="Profilo"

                        val fragment = parentFragmentManager.beginTransaction()
                        fragment.replace(R.id.fragment_container, ProfileFragment()).commit()
                    }else{
                        Toast.makeText(activity, "Username e/o password errati!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(activity, "Campi vuoti non ammessi!", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<TextView>(R.id.text_bottom_bar).setOnClickListener {
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, RegisterFragment()).commit()
        }
    }
}

