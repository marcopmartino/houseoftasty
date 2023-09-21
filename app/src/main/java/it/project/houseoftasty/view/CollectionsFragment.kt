package it.project.houseoftasty.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.CollectionAdapter
import it.project.houseoftasty.databinding.FragmentCollectionsBinding
import it.project.houseoftasty.viewModel.CollectionsViewModel

class CollectionsFragment : Fragment() {

    private val collectionsViewModel : CollectionsViewModel by viewModels()
    private lateinit var binding: FragmentCollectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = collectionsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Raccolte")

        /* Imposta il "layoutManager" e l'"adapter" per la RecyclerView;
        passa all'Adapter la funzione da eseguire al click sul singolo elemento della RecyclerView */
        val recyclerView: RecyclerView = binding.recyclerView
        val collectionAdapter = CollectionAdapter(requireContext(), resources) { collectionId -> adapterOnClick(collectionId) }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = collectionAdapter

        /* Imposta un osservatore sulla lista di raccolte, per aggiornare la lista dinamicamente. */
        collectionsViewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di ricette da mostrare */
                collectionAdapter.submitList(it)

                Log.d("Adapter", it.toString())
            }
        }

        /* Imposta un clickListener sul F.A.B */
        val fab = binding.floatingActionButton
        fab.setOnClickListener {
            fab.setFabVisibility(false)
            fabOnClick()
        }


    }

    override fun onStart() {
        super.onStart()
        // Ricarica le raccolte - utile in particolare dopo aver modificato una raccolta
        collectionsViewModel.reinitialize()
    }

    /* Naviga verso CollectionFormFragment al click sul F.A.B */
    private fun fabOnClick() {
        navigateTo(CollectionsFragmentDirections
            .actionNavCollectionsToCollectionFormFragment(null, null))
    }


    /* Naviga verso CookbookFragment al click su un elemento della RecyclerView. */
    private fun adapterOnClick(collectionId: String) {
        navigateTo(CollectionsFragmentDirections
            .actionNavCollectionsToCollectionDetailsFragment(collectionId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }

}