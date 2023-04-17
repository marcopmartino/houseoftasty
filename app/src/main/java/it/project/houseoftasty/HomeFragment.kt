package it.project.houseoftasty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        //Bottone in home per andare ai prodotti
        view.findViewById<Button>(R.id.button_home_prodotti).setOnClickListener{
            val fragment = parentFragmentManager.beginTransaction()
            if(firebaseUser!=null){
                fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
            }else{
                fragment.replace(R.id.fragment_container, LoginFragment()).commit()
            }
        }
    }
}