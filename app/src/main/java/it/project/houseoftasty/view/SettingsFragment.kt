package it.project.houseoftasty.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentSettingsBinding
import it.project.houseoftasty.utility.isUserAuthenticated
import it.project.houseoftasty.viewModel.AccountDeleteViewModel
import it.project.houseoftasty.viewModel.SettingsViewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel : SettingsViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Modifico il titolo della Action Bar
        val mainActivity = (activity as MainActivity)
        mainActivity.setActionBarTitle("Reset Account")

        // Naviga verso la schermata con le informazioni sull'applicazione
        binding.itemAbout.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToAboutFragment())
        }

        // Naviga verso la schermata con le impostazioni sulle notifiche
        binding.itemNotificationsManagement.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToNotificationSettingsFragment())
        }

        // Naviga verso la schermata con le impostazioni di avvio
        binding.itemStartSettings.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToStartSettingsFragment())
        }

        // Naviga verso la schermata con le informazioni sull'account dell'utente
        binding.itemAccountInfo.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToAccountInfoFragment())
        }

        // Naviga verso il profilo
        binding.itemProfile.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToProfileFragment())
        }

        // Naviga verso la schermata di accesso
        binding.itemLogout.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToNavLogout())
        }

        // Naviga verso la schermata per il reset dell'account
        binding.itemAccountReset.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToAccountResetFragment())
        }

        // Naviga verso la schermata per l'eliminazione dell'account
        binding.itemAccountDeletion.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToAccountDeleteFragment())
        }

        // Naviga verso la schermata con le Note di Rilascio
        binding.itemReleaseNotes.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToReleaseNotesFragment())
        }

        // Naviga verso la schermata con i Termini di Utilizzo
        binding.itemTermsOfService.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToTermsOfServiceFragment())
        }

        // Naviga verso la schermata con l'informativa sulla Privacy
        binding.itemPrivacyPolicy.setOnClickListener {
            navigateTo(SettingsFragmentDirections.actionNavSettingsToPrivacyPolicyFragment())
        }

        return binding.root
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}