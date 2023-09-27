package it.project.houseoftasty.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.PublicRecipeAdapter
import it.project.houseoftasty.databinding.FragmentPublicProfileBinding
import it.project.houseoftasty.utility.ImageLoader
import it.project.houseoftasty.viewModel.PublicProfileViewModel
import it.project.houseoftasty.viewModel.PublicProfileViewModelFactory

class PublicProfileFragment: Fragment() {

    private val publicProfileViewModel: PublicProfileViewModel by viewModels {
        // Factory class constructor
        PublicProfileViewModelFactory(args.userId)
    }

    private lateinit var binding: FragmentPublicProfileBinding

    // Parametri passati al Fragment dalla navigazione
    private val args: PublicProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentPublicProfileBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = publicProfileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        val mainActivity: MainActivity = (activity as MainActivity)
        mainActivity.setActionBarTitle("Profilo")

        // Taglia l'immagine oltre i bordi (gli angoli dell'ImageView sono arrotondati)
        binding.imageProfile.clipToOutline = true

        // Osservatore su "profileLiveData" per caricare l'immagine di profilo
        publicProfileViewModel.profileLiveData.observe(viewLifecycleOwner) {

            /* Se non è stato passato nessun userId come argomento, significa che l'utente autenticato
            * si trova nella schermata del suo profilo. In tal caso ha la possibilità di vedere
            * ulteriori dettagli del suo profilo. */
            if (publicProfileViewModel.isCurrentUser) {
                mainActivity.setActionBarTitle("Il tuo profilo")

                binding.topOfFragment.setOnClickListener {
                    navigateTo(PublicProfileFragmentDirections.actionNavProfileToProfileFragment())
                }

                val fab = binding.floatingActionButton
                fab.setFabVisibility(true)

                /* Imposta un clickListener sul F.A.B */
                fab.setOnClickListener {
                    fab.setFabVisibility(false)
                    navigateTo(PublicProfileFragmentDirections.actionNavProfileToRecipePublishFragment())
                }
            } else {
                publicProfileViewModel.profileLiveData.value?.username.also { username ->
                    if (!username.isNullOrEmpty())

                    // Modifico il titolo della Action Bar
                        mainActivity.setActionBarTitle("Profilo di $username")
                }
            }

            // Carica l'immagine
            if (it.imageReference != null)
                ImageLoader.loadFromReference(
                    requireContext(),
                    it.imageReference,
                    binding.imageProfile,
                    R.drawable.img_peopleiconcol,
                    R.drawable.img_peopleiconcol
                )
        }

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        val publicRecipeAdapter = PublicRecipeAdapter(requireContext(), resources) { recipeId -> adapterOnClick(recipeId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = publicRecipeAdapter

        /* Imposta un osservatore sulla lista di ricette, per aggiornare la lista dinamicamente. */
        publicProfileViewModel.recipeLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                publicRecipeAdapter.submitList(it)
            }
        }

    }

    /* Naviga verso RecipePostFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(recipeId: String) {
        navigateTo(PublicProfileFragmentDirections.actionNavProfileToRecipePostFragment(recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}