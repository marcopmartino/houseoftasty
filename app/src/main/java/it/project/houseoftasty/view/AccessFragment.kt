package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import it.project.houseoftasty.databinding.FragmentAccessBinding

class AccessFragment : Fragment() {

    private lateinit var binding: FragmentAccessBinding


    override fun onStart() {
        super.onStart()
        (activity as AccessActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AccessActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ottengo un riferimento al NavigationController
        val navController = requireView().findNavController()

        // Imposto un listener sul pulsante di Login
        binding.buttonLogin.setOnClickListener {
            navController.navigate(
                AccessFragmentDirections.actionAccessFragmentToLoginFragment()
            )
        }

        // Imposto un listener sul pulsante di Registrazione
        binding.buttonRegister.setOnClickListener {
            navController.navigate(
                AccessFragmentDirections.actionAccessFragmentToRegisterFragment()
            )
        }

        // Imposto un listener sulla barra in basso
        binding.bottomFrameLayout.setOnClickListener {
            navController.navigate(
                AccessFragmentDirections.actionAccessFragmentToMainActivity()
            )
        }

    }

}