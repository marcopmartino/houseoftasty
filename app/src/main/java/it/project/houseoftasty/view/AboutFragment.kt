package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentAboutBinding
import it.project.houseoftasty.databinding.FragmentAccountInfoBinding

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        // Modifico il titolo della Action Bar
        val mainActivity = (activity as MainActivity)
        mainActivity.setActionBarTitle("About")

        return binding.root
    }

}