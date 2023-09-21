package it.project.houseoftasty.view


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.ProductAdapter
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.databinding.FragmentMyProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel
import kotlinx.coroutines.*


class MyProductFragment() : Fragment(){

    private val productViewModel : ProductViewModel by viewModels()

    private lateinit var binding: FragmentMyProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductBinding.inflate(inflater)
        binding.viewModel = productViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    /**
     * Metodo chiamato subito dopo che la vista e' stata creata.
     * Si occupa di inizializzare la vista ed il suo contenuto. In particolare:
     * - Inizializza l'oggetto SwipeRefreshLayout per permettere l'aggiornamento della lista dei prodotti.
     * - Inizializza l'oggetto RecyclerView per mostrare i prodotti.
     * - Legge i dati dei prodotti dal database locale o remoto, a seconda se l'utente e' autenticato o meno.
     * - Aggiunge un decorator per mostrare una linea divisoria tra i prodotti sulla RecyclerView.
     * - Aggiunge un listener al pulsante per aggiungere un nuovo prodotto, per navigare alla schermata di aggiunta prodotto.
     * - Aggiunge un listener al tasto back per tornare alla home.
     * - Aggiunge un listener al SwipeRefreshLayout per permettere di aggiornare i dati dei prodotti.
     *
    */
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setActionBarTitle("I miei prodotti")

        val recyclerview: RecyclerView = binding.productList
        val productAdapter =
            ProductAdapter(requireContext(), resources) { product -> adapterOnClick(product) }
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.adapter = productAdapter

        productViewModel.productLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di prodotti da mostrare */
                productAdapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {

            val action: NavDirections =
                MyProductFragmentDirections.actionMyProductFragmentToProductFormFragment(null)
            requireView().findNavController().navigate(action)
        }
    }

    private fun adapterOnClick(product: Product) {
        val productId = product.id

        Log.d("TAG",product.id.toString())

        if (productId != null) {
            Log.d("TAG",productId)
            val action : NavDirections =
                MyProductFragmentDirections.actionMyProductFragmentToProductFormFragment(productId)
            requireView().findNavController().navigate(action)
        }
    }

}