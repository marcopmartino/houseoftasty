package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentNotificationSettingsBinding

class NotificationSettingsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = (activity as MainActivity)
        val sharedPreferences = mainActivity.getSharedPreferences()
        val switch = binding.switchNotifications

        fun updateSharedPreferences(bool: Boolean) {
            sharedPreferences.edit().putBoolean("notifications", bool).apply()
        }

        // Modifico il titolo della Action Bar
        mainActivity.setActionBarTitle("Gestione delle Notifiche")

        // Inizializzo lo switch
        switch.isChecked = sharedPreferences.getBoolean("notifications", true)

        // Listener sullo switch per aggiornare le SharedPreferences
        switch.setOnCheckedChangeListener { _, booleanValue ->
            updateSharedPreferences(booleanValue)
            if (booleanValue)
                mainActivity.enableNotifications()
            else
                mainActivity.disableNotifications()
        }


    }



}