package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")

        view.findViewById<Button>(R.id.button_registrati).setOnClickListener{
            val username = view.findViewById<EditText>(R.id.username_register).text.toString()
            val email = view.findViewById<EditText>(R.id.email_register).text.toString()
            val nome = view.findViewById<EditText>(R.id.nome_register).text.toString()
            val cognome = view.findViewById<EditText>(R.id.cognome_register).text.toString()
            val password = view.findViewById<EditText>(R.id.password_register).text.toString()
            val chkPassword = view.findViewById<EditText>(R.id.confirmPassword_register).text.toString()

            if(username.isNotEmpty() && email.isNotEmpty() && nome.isNotEmpty() &&
                cognome.isNotEmpty() && password.isNotEmpty() && chkPassword.isNotEmpty()){
                if(isValidEmail(email)) {
                    if (password == chkPassword) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                createDbData(username, email, nome, cognome)
                                val fragment = parentFragmentManager.beginTransaction()
                                fragment.replace(R.id.fragment_container, LoginFragment()).commit()
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

        view.findViewById<TextView>(R.id.text_bottom_bar).setOnClickListener{
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, LoginFragment()).commit()
        }
    }

    private fun isValidEmail(mail: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    private fun createDbData(username: String, email:String, nome:String, cognome:String ){
        try {
            val user = HashMap<String, Any>()
            user.put("username",username)
            user.put("email", email)
            user.put("nome", nome)
            user.put("cognome", cognome)
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

