package it.project.houseoftasty.network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.utility.getAndroidId
import it.project.houseoftasty.utility.getCurrentUserIdOrAndroidId
import it.project.houseoftasty.utility.isUserAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileNetwork : StorageNetwork("immagini_profili") {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")

    suspend fun getUserData(userId: String = firebaseAuth.uid.toString()): Profile {

        lateinit var document: DocumentSnapshot
        lateinit var profile: Profile

        // Recupera i dati sul profilo (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = usersReference.document(userId).get().await()
        }

        // Converte lo snapshot del documento in un oggetto Profile
        withContext(Dispatchers.Default) {
            document.toObject(Profile::class.java).also {
                if (it != null) {
                    profile = it
                }
            }

            profile.id = userId
        }

        // Prende un riferimento al file immagine della ricetta (non scarica il file)
        if (profile.boolImmagine)
            profile.imageReference = getFileReference(userId)

        return profile
    }

    suspend fun getUserUsername(userId: String = firebaseAuth.uid.toString()): String {

        lateinit var document: DocumentSnapshot

        // Recupera i dati sul profilo (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = usersReference.document(userId).get().await()
        }

        return document.get("username").toString()
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        login(email, password).addOnCompleteListener {
            if (it.isSuccessful) onSuccess.invoke() else onFailure.invoke()
        }
    }

    suspend fun createUser(email: String, password: String) {
        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }
        Log.d("AndroidRuntime", "Utente: " + firebaseAuth.currentUser?.uid.toString())
    }

    suspend fun addUser(user: Profile) {
        Log.d("Insert", user.toString())

        withContext(Dispatchers.IO) {
            usersReference.document(firebaseAuth.currentUser!!.uid).set(user).await()
        }
    }

    suspend fun userExists(userId: String): Boolean {
        val exists: Boolean
        withContext(Dispatchers.IO) {
            exists = usersReference.document(userId).get().await().exists()
        }
        return exists
    }

    suspend fun userNotExists(userId: String): Boolean {
        return !userExists(userId)
    }

    suspend fun createEmptyUser(userId: String) {
        withContext(Dispatchers.IO) {
            usersReference.document(userId).set(emptyMap<String, Any>())
        }
    }

    suspend fun createUserIfNotExists(userId: String) {
        if (userNotExists(userId)) createEmptyUser(userId)
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

    suspend fun deleteUser() {
        usersReference.document(firebaseAuth.getCurrentUserIdOrAndroidId()).delete()
        firebaseAuth.currentUser?.delete()
    }

    fun getAndroidId(): String {
        return firebaseAuth.getAndroidId()
    }

    /* Factory method */
    companion object {
        private var INSTANCE: ProfileNetwork? = null
        private var NEW_INSTANCE = ProfileNetwork()

        fun getDataSource(newInstance: Boolean = true): ProfileNetwork {
            return synchronized(ProfileNetwork::class) {
                if (newInstance) NEW_INSTANCE
                else {
                    val instance = INSTANCE ?: NEW_INSTANCE
                    INSTANCE = instance
                    instance
                }
            }
        }
    }

}
