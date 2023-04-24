package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentProfileBinding
import it.project.houseoftasty.viewModel.UserViewModel
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference

    private val userModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)

        firebaseDb.get().addOnSuccessListener{
            userModel.loadData(it.data?.get("username").toString(),it.data?.get("nome").toString(),
                it.data?.get("cognome").toString(), it.data?.get("email").toString())
            binding.userData = userModel
        }.addOnFailureListener {
            userModel.loadData("-", "-","-","-")
            binding.userData = userModel
            Toast.makeText(activity, "Impossibile recuperare i dati. Controllare la propria connessione di rete!!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
        }

        view.findViewById<TextView>(R.id.btnEditProfile).setOnClickListener {
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, EditProfileFragment()).commit()
        }
    }

}