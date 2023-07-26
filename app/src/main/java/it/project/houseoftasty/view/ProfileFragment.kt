package it.project.houseoftasty.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentProfileBinding
import it.project.houseoftasty.viewModel.UserViewModel
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference
    private lateinit var firebaseStorage: StorageReference

    private val maxImageSize: Long = 1024*1024

    private val userModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root

    }

    /**
     * Recupera i dati dell'utente dal database Firebase e li visualizza nella vista.
     * In caso di errore durante il recupero dei dati, visualizza un messaggio di errore all'utente.
     * Nel caso in cui l'utente prema il pulsante "Modifica profilo", sostituisce la visualizzazione corrente con la vista di modifica profilo.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Profilo")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
        firebaseStorage = Firebase.storage.reference

        firebaseDb.get().addOnSuccessListener{
            userModel.loadData(it.data?.get("username").toString(),it.data?.get("nome").toString(),
                it.data?.get("cognome").toString(), it.data?.get("email").toString())
            firebaseStorage.child("immagini_profili/${firebaseAuth.currentUser!!.uid}").getBytes(maxImageSize).addOnSuccessListener{ img->
                view.findViewById<ImageView>(R.id.profileImage).setImageURI(null)
                view.findViewById<ImageView>(R.id.profileImage).setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.size))
            }.addOnFailureListener{
                Log.d("TEST", "FALLIMENTO")
            }
            binding.userData = userModel
        }.addOnFailureListener {
            userModel.loadData("-", "-","-","-")
            binding.userData = userModel
            Toast.makeText(activity, "Impossibile recuperare i dati. Controllare la propria connessione di rete!!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
        }

        binding.btnEditProfile.setOnClickListener {
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, EditProfileFragment()).commit()
        }
    }

}