package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.project.houseoftasty.R
class TermsOfServiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Imposta il titolo dell'ActionBar
        (activity as MainActivity).setActionBarTitle("Termini di Utilizzo")

        // Inflating del layout
        return inflater.inflate(R.layout.fragment_terms_of_service, container, false)
    }
}