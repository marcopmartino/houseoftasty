package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.R

class LogoutFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        val navView = activity?.findViewById<NavigationView>(R.id.navView)
        navView!!.menu.clear()
        navView.inflateMenu(R.menu.menu_unauthenticated)
        (activity as AppCompatActivity).supportActionBar?.title ="Login"
        val fragment = activity?.supportFragmentManager?.beginTransaction()
        fragment?.replace(R.id.fragment_container, LoginFragment())?.commit()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
}