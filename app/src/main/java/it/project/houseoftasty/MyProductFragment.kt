package it.project.houseoftasty


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentMyProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel


class MyProductFragment : Fragment(), Communicator {

    private lateinit var binding: FragmentMyProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductBinding.inflate(inflater)
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

        firebaseDb.get().addOnSuccessListener { documents ->
            for(document in documents){
                val productModel = ProductViewModel()
                productModel.setData(document.id,document.get("nome").toString(),document.get("quantita").toString(),
                    document.get("misura").toString(), document.get("scadenza").toString())
                data.add(productModel)
            }
            val adapter = ProductAdapter(data, this)
            recyclerview.adapter=adapter
        }.addOnFailureListener{
            Toast.makeText(activity, "Errore nel caricamento dei dati!!", Toast.LENGTH_LONG).show()
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

}