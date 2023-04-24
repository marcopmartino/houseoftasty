package it.project.houseoftasty

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.database.ProductDatabase
import it.project.houseoftasty.databaseInterface.ProductDao
import it.project.houseoftasty.databinding.FragmentAddProductBinding
import kotlinx.coroutines.*
import java.util.*

class AddProductFragment : Fragment() {


    private lateinit var binding: FragmentAddProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: CollectionReference
    private lateinit var productDao: ProductDao

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")
            .document(firebaseAuth.currentUser!!.uid).collection("products")

        productDao = ProductDatabase.getInstance(requireContext()).productDAO()

        val spinner: Spinner = view.findViewById(R.id.quantitaMisura)
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Imposta i valori nello spinner, l'elemento "-" corrisponde a nessuna unita di misura
        activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.array_misure,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }

        //Mostra la data di scadenza selezionata
        view.findViewById<ImageButton>(R.id.dataButton).setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                // Mostra data selezionata nella TextView
                view.findViewById<TextView>(R.id.dataScadenza).text =
                    "$dayOfMonth/$monthOfYear/$year"
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = false
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        //Rimuove la data inserita se si spunta la checkbox di un prodotto senza scadenza
        view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).setOnClickListener{
            view.findViewById<TextView>(R.id.dataScadenza).text = "--/--/----"
        }

        // Callback per tornare indietro, per funzionare il fragment deve essere caricato nella sua interezza.
        requireActivity().onBackPressedDispatcher.addCallback(activity) {
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
        }

        //Aggiunge prodotto al DB
        view.findViewById<Button>(R.id.button_addProduct).setOnClickListener {
            val nome = view.findViewById<EditText>(R.id.dataNome).text.toString()
            val quantita = view.findViewById<EditText>(R.id.dataQuantita).text.toString()
            var misura = view.findViewById<Spinner>(R.id.quantitaMisura).selectedItem.toString()
            var scadenza = view.findViewById<TextView>(R.id.dataScadenza).text.toString()
            val check = view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked

            if (nome.isNotEmpty() && quantita.isNotEmpty() && misura.isNotEmpty()) {
                if (!check && scadenza == "--/--/----") {
                    Toast.makeText(activity, "Inserire una data di scadenza!", Toast.LENGTH_SHORT).show()
                } else {
                    if (misura == "-") misura = ""
                    if (check) scadenza = "-"

                    runBlocking {
                        addProductDb(nome, quantita, misura, scadenza)
                        delay(1000)
                    }

                    val fragment = parentFragmentManager.beginTransaction()
                    fragment.replace(R.id.fragment_container, MyProductFragment()).commit()

                }
            } else {
                Toast.makeText(
                    activity,
                    "I campi devono essere tutti riempiti!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    //Funzione per aggiungere prodotti al db
    private fun addProductDb(nome: String, quantita: String, misura: String, scadenza: String) {
        try {
            val product = HashMap<String, Any>()

            product["nome"] = nome
            product["quantita"] = quantita
            product["misura"] = misura
            product["scadenza"] = scadenza

            firebaseDb.add(product).addOnSuccessListener {
                val temp = Product(it.id, nome, quantita, misura, scadenza)
                lifecycleScope.launch(Dispatchers.IO){
                    productDao.insert(temp)
                }
                Toast.makeText(activity, "Prodotto aggiunto!!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception: java.lang.Exception ->
                Toast.makeText(activity, exception.toString(), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
