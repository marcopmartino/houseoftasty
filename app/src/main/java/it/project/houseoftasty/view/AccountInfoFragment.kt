package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentAccountInfoBinding
import it.project.houseoftasty.utility.getAndroidId
import it.project.houseoftasty.utility.getCurrentUserId
import it.project.houseoftasty.utility.isUserAuthenticated
import it.project.houseoftasty.utility.textToString

class AccountInfoFragment : Fragment() {

    private lateinit var binding: FragmentAccountInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentAccountInfoBinding.inflate(inflater, container, false)

        // Modifico il titolo della Action Bar
        val mainActivity = (activity as MainActivity)
        mainActivity.setActionBarTitle("Info Account")

        // Imposta il testo di "accountInfoTextView" usando il template "accountInfoTextTemplate"
        fun setAccountInfoText(identifier: String, identifierType: String, authenticationMode: String, restrictionText: String) {
            binding.accountInfoIdentifierContent.text = identifier
            binding.accountInfoIdentifierTypeContent.text = identifierType
            binding.accountInfoAuthenticationModeContent.text = authenticationMode
            binding.accountInfoRestrictionsContent.text = restrictionText

        }

        // Modifico il testo con le informazioni sull'account in base al tipo di account
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.isUserAuthenticated())
            setAccountInfoText(
                firebaseAuth.getCurrentUserId(),
                "Codice generato automaticamente",
                "Login con Email e Password",
                "Nessuna"
            )
        else
            setAccountInfoText(
                firebaseAuth.getAndroidId(),
                "Android ID",
                "Riconoscimento del dispositivo",
                resources.getString(R.string.accountInfoRestrictionList)
            )



        return binding.root
    }

}