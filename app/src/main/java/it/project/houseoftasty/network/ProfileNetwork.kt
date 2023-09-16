package it.project.houseoftasty.network

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileNetwork : FirebaseNetwork("users") {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //lateinit var firestoreReference: DocumentReference

    suspend fun getUserData(): Profile {

        lateinit var document: DocumentSnapshot
        lateinit var profile: Profile

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = firestoreReference.document(firebaseAuth.uid.toString()).get().await()
        }

        // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeList"
        withContext(Dispatchers.Default) {
            profile = Profile(
                firebaseAuth.uid.toString(),
                document.data!!["email"].toString(),
                null,
                null,
                null,
                null,
                document.data!!["nome"].toString(),
                document.data!!["cognome"].toString(),
                document.data!!["username"].toString()
            )
        }
        return profile
    }

    suspend fun addUser(user: Profile) {
        Log.d("Insert", user.toString())

        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(user.mail!!, user.password!!)
                .addOnFailureListener{return@addOnFailureListener}.await()
            val temp = HashMap<String, Any>()
            temp["username"] = user.username!!
            temp["email"] = user.mail!!
            temp["nome"] = user.nome!!
            temp["cognome"] = user.cognome!!

            Log.d("insert", "uid: "+firebaseAuth.currentUser!!.uid.toString())
            firestoreReference.document(firebaseAuth.currentUser!!.uid).set(temp).await()
        }
    }

    suspend fun updateUser(user:Profile) {
        withContext(Dispatchers.IO) {
            val credential: AuthCredential = EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, user.password!!)
            firebaseAuth.currentUser!!.reauthenticate(credential).addOnSuccessListener {
                if(user.newPsw!!.isNotEmpty() && user.chkNewPsw!!.isNotEmpty() && user.newPsw.equals(user.chkNewPsw))
                    firebaseAuth.currentUser!!.updatePassword(user.newPsw!!)
                if(firebaseAuth.currentUser!!.email != user.mail!!)
                    firebaseAuth.currentUser!!.updateEmail(user.mail!!)
            }.addOnFailureListener{return@addOnFailureListener}

            Log.d("Update", user.toString())
            val temp = HashMap<String, Any>()
            temp["username"] = user.username!!
            temp["email"] = user.mail!!
            temp["nome"] = user.nome!!
            temp["cognome"] = user.cognome!!
            firestoreReference.document(firebaseAuth.currentUser!!.uid).update(temp)
        }
    }

}
