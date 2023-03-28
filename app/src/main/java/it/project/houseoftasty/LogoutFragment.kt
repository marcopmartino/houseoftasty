package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class LogoutFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        var navView = activity?.findViewById<NavigationView>(R.id.navView)
        navView!!.menu.clear()
        navView.inflateMenu(R.menu.nav_item)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
}