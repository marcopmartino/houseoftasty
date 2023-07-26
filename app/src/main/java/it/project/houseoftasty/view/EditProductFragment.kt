package it.project.houseoftasty.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.R
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.dataModel.ProductId
import it.project.houseoftasty.database.HouseTastyDb
import it.project.houseoftasty.databaseInterface.HouseTastyDao
import it.project.houseoftasty.databinding.FragmentEditProductBinding
import it.project.houseoftasty.viewModel.ProductViewModel
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
  * Fragment che consente di modificare le informazioni di un prodotto,
  * caricando i dati dal database Firebase e dalla base di dati interna dell'app,
  * e aggiornando i dati in entrambe le basi di dati dopo le modifiche.
 **/
class EditProductFragment : Fragment() {

    private lateinit var binding: FragmentEditProductBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: DocumentReference

    private val productModel: ProductViewModel by viewModels()

    private lateinit var nome: String
    private lateinit var quantita: String
    private lateinit var misura: String
    private lateinit var scadenza: String
    private lateinit var houseTastyDao: HouseTastyDao
    private lateinit var productId: String

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

        productId = arguments?.getString("id")!!

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance().collection("users")
            .document(firebaseAuth.currentUser!!.uid).collection("products")
            .document(productId)

        houseTastyDao = HouseTastyDb.getInstance(requireContext()).houseTastyDAO()

        val spinner: Spinner = view.findViewById(R.id.quantitaMisura)
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        firebaseDb.get().addOnSuccessListener { it ->
            productModel.loadData(
                it.data?.get("nome").toString(), it.data?.get("quantita").toString(),
                it.data?.get("scadenza").toString(), it.data?.get("misura").toString()
            )

            // Imposta data
            if (productModel.scadenza == "-") {
                view.findViewById<TextView>(R.id.dataScadenza).text = "--/--/----"
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = true
            } else {
                view.findViewById<TextView>(R.id.dataScadenza).text = productModel.scadenza
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

                    val position = adapter.getPosition(productModel.unitaMisura)
                    spinner.setSelection(position)

                }
            }

            binding.productData = productModel
        }.addOnFailureListener {
            val data = houseTastyDao.getById(productId)

            productModel.loadData(
                data[0].nome, data[0].quantita,
                data[0].scadenza, data[0].unitaMisura
            )

            // Imposta data
            if (productModel.scadenza == "-") {
                view.findViewById<TextView>(R.id.dataScadenza).text = "--/--/----"
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = true
            } else {
                view.findViewById<TextView>(R.id.dataScadenza).text = productModel.scadenza
                view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked = false
            }
        }.addOnCompleteListener {
            view.findViewById<ProgressBar>(R.id.waitingBar).visibility = View.GONE
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
        view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).setOnClickListener {
            view.findViewById<TextView>(R.id.dataScadenza).text = "--/--/----"
        }

        //Elimina i prodotti
        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button_deleteProduct)
            .setOnClickListener {
                AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setMessage("Eliminare il prodotto?")
                    .setPositiveButton("Conferma") { _, _ ->

                        lifecycleScope.launch(Dispatchers.IO) {
                            houseTastyDao.deleteById(ProductId(productId))
                        }

                        firebaseDb.delete()

                        Toast.makeText(activity, "Prodotto eliminato!", Toast.LENGTH_SHORT).show()
                        val fragment = parentFragmentManager.beginTransaction()
                        fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
                    }
                    .setNegativeButton("Annulla") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }

        //Modifica i prodotti
        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button_editProduct)
            .setOnClickListener {
                nome = view.findViewById<EditText>(R.id.dataTitle).text.toString()
                quantita = view.findViewById<EditText>(R.id.dataQuantita).text.toString()
                misura = view.findViewById<Spinner>(R.id.quantitaMisura).selectedItem.toString()
                scadenza = view.findViewById<TextView>(R.id.dataScadenza).text.toString()
                val check = view.findViewById<CheckBox>(R.id.checkBoxSenzascadenza).isChecked

                runBlocking {
                    updateProduct(check)
                }

                Toast.makeText(activity, "Modifiche applicate con successo!!", Toast.LENGTH_SHORT)
                    .show()

                val fragment = parentFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container, MyProductFragment()).commit()
            }

    }


    /**
     * Aggiorna il prodotto nel database se tutti i campi sono compilati correttamente.
     * Se la spunta "Senza scadenza" non è selezionata e la data di scadenza non è stata inserita, mostra un messaggio di errore.
     * Se la misura è "-", viene impostata su una stringa vuota.
     * Se la spunta "Senza scadenza" è selezionata, la data di scadenza viene impostata su "-".
     * Viene avviata una coroutine per aggiornare il database.
     * Se uno qualsiasi dei campi è vuoto, viene mostrato un messaggio di errore.
    */
    private fun updateProduct(check: Boolean) {
        if (this.nome.isNotEmpty() && quantita.isNotEmpty() && misura.isNotEmpty()) {
            if (!check && scadenza == "--/--/----") {
                Toast.makeText(activity, "Inserire una data di scadenza!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (misura == "-") misura = ""
                if (check) scadenza = "-"
                lifecycleScope.launch(Dispatchers.Main) {
                    updateDbSuspend()
                }
            }
        } else {
            Toast.makeText(activity, "I campi non possono essere vuoti!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *  Funzione ausiliaria per aggiornare il database Firebase.
     **/
    private suspend fun updateDbSuspend() = suspendCoroutine { cont ->
        cont.resume(updateDb())
    }

    /**
     * Aggiorna i dati del prodotto nel database Firebase e nella roomDB
    */
    private fun updateDb() {
        return runBlocking {
            firebaseDb.get().addOnCompleteListener {

                firebaseDb.update("nome", nome)
                firebaseDb.update("quantita", quantita)
                firebaseDb.update("misura", misura)
                firebaseDb.update("scadenza", scadenza)


                lifecycleScope.launch(Dispatchers.IO) {
                    val temp = Product(productId, nome, quantita, misura, scadenza)
                    houseTastyDao.update(temp)
                }
            }
        }
    }

}