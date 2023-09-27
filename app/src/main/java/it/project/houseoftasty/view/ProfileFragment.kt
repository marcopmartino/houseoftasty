package it.project.houseoftasty.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.databinding.FragmentProfileBinding
import it.project.houseoftasty.utility.ImageLoader
import it.project.houseoftasty.viewModel.ProfileViewModel
import it.project.houseoftasty.viewModel.UserViewModel
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireContext().cacheDir.deleteRecursively()
        FirebaseFirestore.getInstance().clearPersistence()

        binding = FragmentProfileBinding.inflate(inflater)
        binding.profileData = profileViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root

    }

    /**
     * Recupera i dati dell'utente dal database Firebase e li visualizza nella vista.
     * In caso di errore durante il recupero dei dati, visualizza un messaggio di errore all'utente.
     * Nel caso in cui l'utente prema il pulsante "Modifica profilo", sostituisce la visualizzazione corrente con la vista di modifica profilo.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Profilo")

        // Taglia l'immagine oltre i bordi (gli angoli dell'ImageView sono arrotondati)
        binding.profileImage.clipToOutline = true


        profileViewModel.profileLiveData.observe(viewLifecycleOwner) {
            if (it.imageReference != null)
                ImageLoader.loadFromReference(
                    requireContext(),
                    it.imageReference,
                    binding.profileImage,
                    R.drawable.img_peopleiconcol,
                    R.drawable.img_peopleiconcol
                )
        }

        /* Imposta un clickListener sul F.A.B */
        val fab = binding.floatingActionButton
        fab.setOnClickListener {
            fab.setFabVisibility(false)
            requireView().findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileFormFragment())
        }
    }

}