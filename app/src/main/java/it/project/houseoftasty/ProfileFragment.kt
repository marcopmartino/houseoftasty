package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentProfileBinding
import it.project.houseoftasty.viewModel.UserViewModel

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
        val view: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.email.toString())

        firebaseDb.get().addOnCompleteListener{
            userModel.loadData(it.result?.data?.get("username").toString(),it.result?.data?.get("nome").toString(),
                               it.result?.data?.get("cognome").toString(), it.result?.data?.get("email").toString())
            binding.userData = userModel
        }

        return view
    }

}