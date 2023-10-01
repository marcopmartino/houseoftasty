package it.project.houseoftasty.view


import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.view.LoginFragmentDirections
import it.project.houseoftasty.databinding.FragmentLoginBinding
import it.project.houseoftasty.network.ProfileNetwork

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var profileNetwork: ProfileNetwork


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as AccessActivity).setActionBarTitle("Login")

        // Ottengo un riferimento al NavigationController
        val navController = requireView().findNavController()

        profileNetwork = ProfileNetwork.getDataSource()

        binding.buttonAccedi.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                profileNetwork.login(email, password,
                    onSuccess =  {
                    navController.navigate(
                        LoginFragmentDirections.actionLoginFragmentToMainActivity())
                    },
                    onFailure = {
                    Toast.makeText(activity, "Username e/o password errati!",
                        Toast.LENGTH_SHORT).show()
                })
            }else{
                Toast.makeText(activity, "Campi vuoti non ammessi!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textBottomBar.setOnClickListener {
            navController.navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }
    }
}

