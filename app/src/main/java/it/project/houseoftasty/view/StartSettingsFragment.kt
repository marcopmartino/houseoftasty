package it.project.houseoftasty.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentStartSettingsBinding
import it.project.houseoftasty.utility.textToString
import it.project.houseoftasty.viewModel.SettingsViewModel
import it.project.houseoftasty.viewModel.StartSettingsViewModel

class StartSettingsFragment : Fragment() {

    private val startSettingsViewModel : StartSettingsViewModel by viewModels()

    private lateinit var binding: FragmentStartSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentStartSettingsBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = startSettingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = (activity as MainActivity)
        val sharedPreferences = mainActivity.getSharedPreferences()

        fun updateSharedPreferences(text: String) {
            sharedPreferences.edit().putString("startActivity", text).apply()
        }

        // Modifico il titolo della Action Bar
        mainActivity.setActionBarTitle("Impostazioni di Avvio")

        binding.radioGroupStartActivity.setOnCheckedChangeListener { _, checkedButtonId ->

            val checkedRadioButtonText = getCheckedRadioButton(checkedButtonId).textToString()

            // Aggiorno il titolo della preview
            updatePreviewTitle(checkedRadioButtonText)

            Log.d("AndroidRuntime RadioText", checkedRadioButtonText)

            // Aggiorno la preview e le preferenze
            when (checkedRadioButtonText) {
                "Home" -> {
                    startSettingsViewModel.setVisiblePreview("Home")
                    updateSharedPreferences("Home")
                }
                "Schermata di accesso" -> {
                    startSettingsViewModel.setVisiblePreview("Access")
                    updateSharedPreferences("Access")
                }
                "Schermata di benvenuto" -> {
                    startSettingsViewModel.setVisiblePreview("Welcome")
                    updateSharedPreferences("Welcome")
                }
            }

            Log.d("AndroidRuntime HomeVisibility", binding.containerPreviewHome.visibility.toString())
            Log.d("AndroidRuntime AccessVisibility", binding.containerPreviewAccess.visibility.toString())
            Log.d("AndroidRuntime WelcomeVisibility", binding.containerPreviewWelcome.visibility.toString())
        }

        // Inizializzazione della schermata in base alle preferenze
        when (sharedPreferences.getString("startActivity", "Home")) {
            "Home" -> {
                binding.radioButtonHome.isChecked = true
            }
            "Access" -> {
                binding.radioButtonAccess.isChecked = true
            }
            "Welcome" -> {
                binding.radioButtonWelcome.isChecked = true
            }
        }
    }

    // Ritorna il Radio Button selezionato
    private fun getCheckedRadioButton(id: Int = binding.radioGroupStartActivity.checkedRadioButtonId): RadioButton {
        return requireView().findViewById(id)
    }

    // Aggiorna il testo della preview con il testo del Radio Button selezionato
    private fun updatePreviewTitle(radioButton: RadioButton = getCheckedRadioButton()) {
        updatePreviewTitle(radioButton.textToString())
    }

    // Modifica il testo della preview
    private fun updatePreviewTitle(text: String) {
        binding.startActivityPreviewTemplate.text =
            resources.getString(R.string.startActivityPreviewTemplate, text)
    }


}