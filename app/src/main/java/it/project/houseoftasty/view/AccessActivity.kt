package it.project.houseoftasty.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ActivityAccessBinding
import it.project.houseoftasty.utility.isAuthenticated

class AccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_access
        )

        // Imposto la toolbar come ActionBar
        setSupportActionBar(binding.contentAccess.accessToolbar)

        // Mostro la freccia per tornare indietro nella ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Se vi è un utente autenticato, eseguo il logout
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser.isAuthenticated()) {
            firebaseAuth.signOut()
        }

    }

    // Funzione per cambiare il titolo della Action Bar
    fun setActionBarTitle(title: String){
        supportActionBar?.title = title
    }

    // Gestisco il comportamento della freccia per tornare indietro
    override fun onSupportNavigateUp(): Boolean {
        findNavController(R.id.fragment_container).popBackStack()
        return true
    }
}