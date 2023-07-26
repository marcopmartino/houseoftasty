package it.project.houseoftasty

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.project.houseoftasty.databinding.FragmentEditProfileBinding
import it.project.houseoftasty.viewModel.UserViewModel
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference
    private lateinit var firebaseStorage: StorageReference

    private val maxImageSize: Long = 1024*1024

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
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title ="Modifica profilo"
        binding = FragmentEditProfileBinding.inflate(inflater)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
        firebaseStorage = Firebase.storage.reference

        runBlocking {
            writeDataSuspend(view)
        }

        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnModificaImmagine).setOnClickListener{
            //val imageZone = view.findViewById<ImageView>(R.id.profileImage)
            firebaseStorage.child("immagini_profili/${firebaseAuth.currentUser!!.uid}.jpg")

            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
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
                updateProfileSuspend()
            }


            if(check){
                Toast.makeText(activity, "Modifiche applicate con successo!!", Toast.LENGTH_SHORT).show()
                val fragment = parentFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }

    }

    /**
     *  Funzione ausiliaria per aggiornare il database Firebase.
     **/
    private suspend fun writeDataSuspend(view: View) = suspendCoroutine {cont->
        cont.resume(writeData(view))
    }

    /**
     *  Scrive i dati dell'utente nel modello dell'utente e carica l'immagine del profilo dallo storage di Firebase
    **/
    private fun writeData(view: View){
        return runBlocking {
            firebaseDb.get().addOnCompleteListener{
                userModel.loadData(it.result?.data?.get("username").toString(),it.result?.data?.get("nome").toString(),
                    it.result?.data?.get("cognome").toString(), it.result?.data?.get("email").toString())
                Log.d("TEST", "??")
                firebaseStorage.child("immagini_profili/${firebaseAuth.currentUser!!.uid}").getBytes(maxImageSize).addOnSuccessListener { img->
                    view.findViewById<ImageView>(R.id.profileImage).setImageURI(null)
                    view.findViewById<ImageView>(R.id.profileImage).setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.size))
                }.addOnFailureListener{
                    Log.d("TEST", "FALLIMENTO")
                }
                binding.userData = userModel
            }.addOnCompleteListener{
                requireView().findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
            }
        }
    }

    /**
     *  Funzione ausiliaria per aggiornare il profilo utente.
    **/
    private suspend fun updateProfileSuspend() = suspendCoroutine { cont ->
        cont.resume(updateProfile())
    }

    /**
     *  Aggiorna il profilo utente con i dati forniti
    **/
    private fun updateProfile() {
       return runBlocking {
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
    }

    /**
     * Aggiorna il database con i dati utente forniti
    **/
    private fun updateDb() {
        if(firebaseAuth.currentUser!!.email != email) {
            firebaseAuth.currentUser!!.updateEmail(email)
            firebaseDb.get().addOnCompleteListener {
                val tempDb = FirebaseFirestore.getInstance().collection("users")
                    .document(email)
                val user = HashMap<String, Any>()
                user["username"] = username
                user["nome"] = nome
                user["cognome"] = cognome
                user["email"] = email
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

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =

        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {

                val imageUri: Uri? = result.data?.data

                val sd = firebaseAuth.currentUser!!.uid

                val uploadTask = firebaseStorage.child("immagini_profili/$sd").putFile(imageUri!!)


                uploadTask.addOnSuccessListener {

                    firebaseStorage.child("immagini_profili/$sd").downloadUrl.addOnSuccessListener {
                        Toast.makeText(activity, "Immagine modificata con successo!!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(activity, "Errore modifica immagine!!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(activity, "Errore modifica immagine!!", Toast.LENGTH_SHORT).show()
                }
            }
        }


}