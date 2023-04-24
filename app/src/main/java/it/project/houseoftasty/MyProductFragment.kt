package it.project.houseoftasty


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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.database.ProductDatabase
import it.project.houseoftasty.databaseInterface.ProductDao
import it.project.houseoftasty.databinding.FragmentMyProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MyProductFragment : Fragment(), Communicator {

    private lateinit var binding: FragmentMyProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: CollectionReference
    private lateinit var productDao: ProductDao
    private lateinit var vista: View
    private lateinit var localData: Deferred<Array<Product>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductBinding.inflate(inflater)
        vista = binding.root
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")
            .document(firebaseAuth.currentUser!!.uid).collection("products")

        val recyclerview = view.findViewById<RecyclerView>(R.id.productList)
        recyclerview.layoutManager = LinearLayoutManager(activity)

        val data = ArrayList<ProductViewModel>()

        productDao = ProductDatabase.getInstance(requireContext()).productDAO()

        firebaseDb.get().addOnSuccessListener { documents ->
            runBlocking {

                readDataSuspend().await()

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
                            productDao.insert(product)
                        }
                        data.add(productModel)
                    }
                } else {
                    //firebaseDb.document().delete()
                    for (element in localData.await()) {
                        // 1° metodo (non ottimale)
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
                            firebaseDb.add(temp)
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
                    val adapter = ProductAdapter(data, this@MyProductFragment)
                    recyclerview.adapter = adapter
                }
            }
        }.addOnFailureListener{
            val products = productDao.getAll()
            for(product in products){
                val productModel = ProductViewModel()
                productModel.setData(product.id, product.nome, product.quantita, product.unitaMisura, product.scadenza)
                data.add(productModel)
            }
            if(data.isNotEmpty()){
                val adapter = ProductAdapter(data, this)
                recyclerview.adapter = adapter
            }
        }.addOnCompleteListener {
            view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
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
    }

    override fun passData(id: String) {
        val bundle = Bundle()
        bundle.putString("id",id)

        val fragment = parentFragmentManager.beginTransaction()
        val frag = EditProductFragment()
        frag.arguments = bundle

        fragment.replace(R.id.fragment_container, frag).addToBackStack(null).commit()

    }

    private suspend fun readDataSuspend() = suspendCoroutine { cont ->
        cont.resume(readData())
    }
    private fun readData(): Deferred<Array<Product>>{

         return lifecycleScope.async(Dispatchers.IO) {
             localData = async { productDao.getAll() }
             return@async localData.await()
         }
    }

}