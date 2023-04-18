package it.project.houseoftasty

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.databinding.FragmentEditProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap

class EditProductFragment : Fragment() {

    private lateinit var binding: FragmentEditProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference

    private val productModel: ProductViewModel by viewModels()

    private lateinit var nome: String
    private lateinit var quantita: String
    private lateinit var misura: String
    private lateinit var scadenza: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditProductBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getString("id")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")
            .document(firebaseAuth.currentUser!!.uid).collection("products").document(product.toString())

        val spinner: Spinner = view.findViewById(R.id.quantitaMisura)
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        firebaseDb.get().addOnCompleteListener{
            productModel.loadData(it.result?.data?.get("nome").toString(), it.result?.data?.get("quantita").toString())
            misura = it.result?.data?.get("misura").toString()
            scadenza = it.result?.data?.get("scadenza").toString()

            // Imposta data
            if(scadenza == "-"){
                view.findViewById<TextView>(R.id.dataScadenza).text = "--/--/----"
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = true
            }else{
                view.findViewById<TextView>(R.id.dataScadenza).text = scadenza
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = false
            }

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

                    val position = adapter.getPosition(misura)
                    spinner.setSelection(position)

                }
            }

            binding.productData = productModel
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

        //Elimina i prodotti
        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button_deleteProduct).setOnClickListener{
            AlertDialog.Builder(activity)
                .setCancelable(true)
                .setMessage("Eliminare il prodotto?")
                .setPositiveButton("Conferma"){ _, _ ->
                    firebaseDb.delete()

                    Toast.makeText(activity, "Prodotto eliminato!", Toast.LENGTH_SHORT).show()
                    val fragment = parentFragmentManager.beginTransaction()
                    fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
                }
                .setNegativeButton("Annulla"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }

        //Modifica i prodotti
        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button_editProduct).setOnClickListener {
            nome = view.findViewById<EditText>(R.id.dataNome).text.toString()
            quantita = view.findViewById<EditText>(R.id.dataQuantita).text.toString()
            misura = view.findViewById<Spinner>(R.id.quantitaMisura).selectedItem.toString()
            scadenza = view.findViewById<TextView>(R.id.dataScadenza).text.toString()
            val check = view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked

            runBlocking {
                updateProduct(check)
                delay(500)
            }

            Toast.makeText(activity, "Modifiche applicate con successo!!", Toast.LENGTH_SHORT).show()
            val fragment = parentFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
        }

    }

    private fun updateProduct(check: Boolean) {
        if(nome.isNotEmpty() && quantita.isNotEmpty() && misura.isNotEmpty()){
            if(!check && scadenza == "--/--/----"){
                Toast.makeText(activity, "Inserire una data di scadenza!", Toast.LENGTH_SHORT).show()
            }else{
                if(misura == "-") misura=""
                if(check) scadenza = "-"
                runBlocking {
                    updateDb()
                    delay(2000)
                }
            }
        }else{
            Toast.makeText(activity, "I campi non possono essere vuoti!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDb() {
        firebaseDb.get().addOnCompleteListener {
            if(it.result?.data?.get("nome").toString() != nome){
                val tempDb = FirebaseFirestore.getInstance().collection("users")
                    .document(firebaseAuth.currentUser!!.email.toString()).collection("products")
                    .document(nome)
                val product = HashMap<String, Any>()
                product.put("quantita", quantita)
                product.put("misura", misura)
                product.put("scadenza", scadenza)
                product.put("nome", nome)
                tempDb.set(product)
                firebaseDb.delete()
            }else{
                firebaseDb.update("quantita", quantita)
                firebaseDb.update("misura", misura)
                firebaseDb.update("scadenza", scadenza)
                firebaseDb.update("nome", nome)
            }
        }
    }

}