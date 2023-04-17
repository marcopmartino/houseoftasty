package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentEditProfileBinding
import it.project.houseoftasty.viewModel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference

    private val userModel: UserViewModel by viewModels()

    private lateinit var username: String
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var email: String
    private lateinit var currentPsw: String
    private lateinit var newPsw: String
    private lateinit var chkNewPsw: String

    private var check = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.title ="Modifica profilo"
        binding = FragmentEditProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.email.toString())

        firebaseDb.get().addOnCompleteListener{
            userModel.loadData(it.result?.data?.get("username").toString(),it.result?.data?.get("nome").toString(),
                it.result?.data?.get("cognome").toString(), it.result?.data?.get("email").toString())
            binding.userData = userModel
        }

        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSalvaModificheOne).setOnClickListener {
            username = view.findViewById<EditText>(R.id.newUsername).text.toString()
            nome = view.findViewById<EditText>(R.id.newNome).text.toString()
            cognome = view.findViewById<EditText>(R.id.newCognome).text.toString()
            email = view.findViewById<EditText>(R.id.newMail).text.toString()
            currentPsw = view.findViewById<EditText>(R.id.currentPassword).text.toString()
            newPsw = view.findViewById<EditText>(R.id.newPassword).text.toString()
            chkNewPsw = view.findViewById<EditText>(R.id.checkNewPassword).text.toString()

            runBlocking {
                updateProfile()
                delay(2000)
            }


            if(check){
                Toast.makeText(activity, "Modifiche applicate con successo!!", Toast.LENGTH_SHORT).show()
                val fragment = parentFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }

    }

    private fun updateProfile() {
        if(username.isNotEmpty() && nome.isNotEmpty() && cognome.isNotEmpty() && email.isNotEmpty()){
            val currEmail = firebaseAuth.currentUser!!.email.toString()
            val credential: AuthCredential = EmailAuthProvider.getCredential(currEmail, currentPsw)
            firebaseAuth.currentUser!!.reauthenticate(credential).addOnSuccessListener {
                if(newPsw.isNotEmpty() && chkNewPsw.isNotEmpty()){
                    if(newPsw == chkNewPsw) {
                        firebaseAuth.currentUser!!.updatePassword(newPsw)
                        updateDb()
                        check = true
                    }else{
                        Toast.makeText(activity, "Le password non coincidono!!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    updateDb()
                    check = true
                }
            }.addOnFailureListener{
                Toast.makeText(activity, "La password corrente non coincide!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "I campi non possono essere vuoti!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDb() {
        if(firebaseAuth.currentUser!!.email != email) {
            firebaseAuth.currentUser!!.updateEmail(email)
            firebaseDb.get().addOnCompleteListener {
                val tempDb = FirebaseFirestore.getInstance().collection("users")
                    .document(email)
                val user = HashMap<String, Any>()
                user.put("username", username)
                user.put("nome", nome)
                user.put("cognome", cognome)
                user.put("email", email)
                tempDb.set(user)
                firebaseDb.delete()
            }
        }else{
            firebaseAuth.currentUser!!.updateEmail(email)
            firebaseDb.update("username", username)
            firebaseDb.update("nome", nome)
            firebaseDb.update("cognome", cognome)
            firebaseDb.update("email", email)
        }
    }


}