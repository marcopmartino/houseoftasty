package it.project.houseoftasty.network

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileNetwork : StorageNetwork("immagini_profili") {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")

    suspend fun getUserData(): Profile {

        lateinit var document: DocumentSnapshot
        lateinit var profile: Profile

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = usersReference.document(firebaseAuth.uid.toString()).get().await()
        }

        // Converte lo snapshot del documento in un oggetto Profile
        withContext(Dispatchers.Default) {
            document.toObject(Profile::class.java).also {
                if (it != null) {
                    profile = it
                }
            }

            profile.id = firebaseAuth.uid
        }

        // Prende un riferimento al file immagine della ricetta (non scarica il file)
        if (profile.boolImmagine)
            profile.imageReference = getFileReference(profile.id.toString())

        return profile
    }

    suspend fun addUser(user: Profile) {
        Log.d("Insert", user.toString())

        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                .addOnFailureListener{return@addOnFailureListener}.await()

            usersReference.document(firebaseAuth.currentUser!!.uid).set(user).await()
        }
    }

    suspend fun updateUser(user: Profile) {
        withContext(Dispatchers.IO) {
            firebaseAuth.currentUser.also { currentUser ->
                if (currentUser != null) {
                    val credential: AuthCredential = EmailAuthProvider.getCredential(currentUser.email!!, user.password!!)
                    currentUser.reauthenticate(credential).addOnSuccessListener {
                        if (!user.newPassword.isNullOrEmpty() && user.newPassword != user.password) {
                            Log.d("UpdateUser", "Inside password update: " + user.newPassword)
                            currentUser.updatePassword(user.newPassword!!)
                        }
                        if (currentUser.email != user.email!!) {
                            Log.d("UpdateUser", "Inside email update: " + user.email)
                            currentUser.updateEmail(user.email!!)
                        }
                    }.addOnFailureListener{return@addOnFailureListener}

                    usersReference.document(currentUser.uid).set(user).await()
                }
            }
        }
    }

    /* Factory method */
    companion object {
        private var INSTANCE: ProfileNetwork? = null

        fun getDataSource(): ProfileNetwork {
            return synchronized(ProfileNetwork::class) {
                val newInstance = INSTANCE ?: ProfileNetwork()
                INSTANCE = newInstance
                newInstance
            }
        }
    }

}
