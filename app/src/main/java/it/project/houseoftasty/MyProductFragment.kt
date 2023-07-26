package it.project.houseoftasty


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.database.HouseTastyDb
import it.project.houseoftasty.databaseInterface.HouseTastyDao
import it.project.houseoftasty.databinding.FragmentMyProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MyProductFragment : Fragment(), Communicator {

    private lateinit var binding: FragmentMyProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: CollectionReference
    private lateinit var houseTastyDao: HouseTastyDao
    private lateinit var vista: View
    private lateinit var localData: Deferred<Array<Product>>
    private lateinit var products: Deferred<Array<Product>>
    private lateinit var adapter: ProductAdapter
    private lateinit var data: ArrayList<ProductViewModel>
    private lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductBinding.inflate(inflater)
        vista = binding.root
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

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        firebaseAuth = FirebaseAuth.getInstance()

        data = ArrayList()

        recyclerview = view.findViewById(R.id.productList)
        recyclerview.layoutManager = LinearLayoutManager(activity)

        houseTastyDao = HouseTastyDb.getInstance(requireContext()).houseTastyDAO()

        if(firebaseAuth.currentUser != null){
            firebaseDb = FirebaseFirestore.getInstance().collection("users")
                .document(firebaseAuth.currentUser!!.uid).collection("products")
            firebaseDb.get().addOnSuccessListener { documents ->

                runBlocking {
                    readDataAsync().await()
                    loadInViewData(documents)
                }

            }.addOnFailureListener{
                runBlocking {
                    readLocalData()
                }
            }.addOnCompleteListener {
                view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
            }
        }else{
            runBlocking {
                readLocalData()
            }
        }

        val dividerItemDecoration = DividerItemDecoration(
            recyclerview.context,
            DividerItemDecoration.VERTICAL
        )

        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(requireContext(),R.drawable.product_divider)!!
        )
        recyclerview.addItemDecoration(dividerItemDecoration)

        view.findViewById<FloatingActionButton>(R.id.addProduct).setOnClickListener{

            //Navigation.findNavController(view).navigate(R.id.action_myProductFragment_to_addProductFragment)

            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, AddProductFragment()).commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(activity) {
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, HomeFragment()).commit()
        }

        swipeRefresh.setOnRefreshListener {
            data.clear()
            if(firebaseAuth.currentUser != null){
                firebaseDb = FirebaseFirestore.getInstance().collection("users")
                    .document(firebaseAuth.currentUser!!.uid).collection("products")
                firebaseDb.get().addOnSuccessListener { documents ->

                    runBlocking {
                        readDataAsync().await()
                        loadInViewData(documents)
                    }

                }.addOnFailureListener{
                    runBlocking {
                        readLocalData()
                    }
                }.addOnCompleteListener {
                    view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
                }
            }else{
                runBlocking {
                    readLocalData()
                }
            }
            recyclerview.adapter!!.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }
    }

    /**
        * Sovrascrive il metodo per passare i dati
        * @param id ID del prodotto selezionato
     * */
    override fun passData(id: String) {
        val bundle = Bundle()
        bundle.putString("id",id)

        val fragment = parentFragmentManager.beginTransaction()
        val frag = EditProductFragment()
        frag.arguments = bundle

        fragment.replace(R.id.fragment_container, frag).addToBackStack(null).commit()

    }

    /**
     * Metodo ausiliaro per sospendere il sistema durante la lettura dei dati dal database locale
     */
    private suspend fun readDataSuspendAsync() = suspendCoroutine {
        it.resume(readDataAsync())
    }

    /**
     * Legge i dati in modo asincrono dal database locale e restituisce un array di oggetti Product.
     */
    private fun readDataAsync(): Deferred<Array<Product>> {

        return lifecycleScope.async(Dispatchers.IO) {
            localData = async { houseTastyDao.getAll() }
            return@async localData.await()
        }
    }

    /**
     * Questo metodo carica i dati nella recyclerView. Se non ci sono dati nel database locale,
     * li recupera dal database Firebase e li inserisce sia nel database locale che nel modello di vista.
     * Se invece ci sono dati nel database locale, li confronta con quelli presenti nel database Firebase e aggiorna il database Firebase
     * con i dati locali (solo se ci sono differenze) e riempie la recyclerView.
     * @param documents risultato della query al database remoto
    */
    private suspend fun loadInViewData(documents: QuerySnapshot){

        if (localData.await().isEmpty()) {
            for (document in documents) {
                val productModel = ProductViewModel()
                productModel.setData(
                    document.id,
                    document.get("nome").toString(),
                    document.get("quantita").toString(),
                    document.get("misura").toString(),
                    document.get("scadenza").toString()
                )
                val product = Product(
                    productModel.id,
                    productModel.nome,
                    productModel.quantita,
                    productModel.unitaMisura,
                    productModel.scadenza
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    houseTastyDao.insert(product)
                }
                data.add(productModel)
            }
        } else {
            for (element in localData.await()) {
                var check = true
                for (document in documents) {
                    if (element.id == document.id) {
                        firebaseDb.document(document.id)
                            .update("nome", element.nome)
                        firebaseDb.document(document.id)
                            .update("quantita", element.quantita)
                        firebaseDb.document(document.id)
                            .update("misura", element.unitaMisura)
                        firebaseDb.document(document.id)
                            .update("scadenza", element.scadenza)
                        check = false
                        break
                    }
                }
                if (check) {
                    val temp = HashMap<String, Any>()
                    temp["nome"] = element.nome
                    temp["quantita"] = element.quantita
                    temp["misura"] = element.unitaMisura
                    temp["scadenza"] = element.scadenza
                    firebaseDb.document(element.id).set(temp)
                }
                val productModel = ProductViewModel()
                productModel.setData(
                    element.id,
                    element.nome,
                    element.quantita,
                    element.unitaMisura,
                    element.scadenza
                )
                data.add(productModel)
            }
        }

        adapter = ProductAdapter(data, this@MyProductFragment)
        recyclerview.adapter = adapter
    }

    /**
    * Legge i dati locali salvati nel database locale,
    * successivamente aggiunge i dati letti ad una lista di prodotti che verrà poi inserita nella recyclerView.
    */
    private suspend fun readLocalData(){
        readDataSuspendAsync().await()
        for(product in products.await()){
            val productModel = ProductViewModel()
            productModel.setData(product.id, product.nome, product.quantita, product.unitaMisura, product.scadenza)
            data.add(productModel)
        }
        adapter = ProductAdapter(data, this@MyProductFragment)
        recyclerview.adapter = adapter

    }

}