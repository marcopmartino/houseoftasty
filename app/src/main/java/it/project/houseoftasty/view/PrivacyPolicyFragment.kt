package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.project.houseoftasty.R

class PrivacyPolicyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Imposta il titolo dell'ActionBar
        (activity as MainActivity).setActionBarTitle("Informativa sulla Privacy")

        // Inflating del layout
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }
}