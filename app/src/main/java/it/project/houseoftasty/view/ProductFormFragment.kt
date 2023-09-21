package it.project.houseoftasty.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentProductFormBinding
import it.project.houseoftasty.utility.extendToTens
import it.project.houseoftasty.validation.EditTextValidator
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.validation.Validator
import it.project.houseoftasty.viewModel.ProductFormViewModel
import it.project.houseoftasty.viewModel.ProfileFormViewModel
import it.project.houseoftasty.viewModel.ProductFormViewModelFactory
import kotlinx.coroutines.*
import java.util.*

class ProductFormFragment : Fragment() {

    private val productFormViewModel: ProductFormViewModel by viewModels {
        // Factory class constructor
        ProductFormViewModelFactory(args.productId)
    }
    lateinit var binding: FragmentProductFormBinding

    // Parametri passati al Fragment dalla navigazione
    private val args: ProductFormFragmentArgs by navArgs()


    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflating e View Binding
        binding = FragmentProductFormBinding.inflate(inflater, container, false)

        // Data Binding
        binding.productData = productFormViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setActionBarTitle(
            if (productFormViewModel.isProductNew.value == true)
                "Nuovo Prodotto" else "Modifica Prodotto"
        )

        // Campi di input della form
        val dataNomeView: EditText = binding.dataNome
        val dataQuantitaMisuraView: Spinner = binding.quantitaMisura
        val dataQuantitaView:EditText = binding.dataQuantita
        val dataScadenzaView: TextView = binding.dataScadenza


        // Costruisce un validatore per la form
        productFormViewModel.generateFormManagerBuilder()
            .addEditTextValidators(
                EditTextValidator.Builder()
                    .setInputView(dataNomeView)
                    .setErrorView(binding.errorNome)
                    .addRules(ValidationRule.MaxLength(30)),
                EditTextValidator.Builder()
                    .setInputView(dataQuantitaView)
                    .setErrorView(binding.errorQuantita)
                    .addRules(ValidationRule.MaxValue(1000)))
            .addUnvalidatedFields(dataQuantitaMisuraView, dataScadenzaView)
            .setSubmitButton(binding.buttonSubmitProduct)
            .setDeleteButton(binding.buttonDeleteProduct)
            .setDeleteConfirmationDialog(requireContext(),
                "Sei sicuro di voler eliminare il prodotto?")
            .addCommonRules(ValidationRule.Required())
            .enableOnTextChangedValidation()
            .enableOnFocusLostValidation()
            .build()

        if (productFormViewModel.isProductNew.value == true){
            binding.buttonDeleteProduct.visibility = View.INVISIBLE
        }

        // Funzione per navigare verso altri Fragment
        fun navigateTo(direction: NavDirections) {
            requireView().findNavController().navigate(direction)
        }

        // Osserva il tipo di operazione completata e determina la navigazione da eseguire
        productFormViewModel.operationCompleted.observe(viewLifecycleOwner) {
            Log.d("EndNavigation", productFormViewModel.operationCompleted.value.toString())

            it?.let {
                when(it.name) {
                    "INSERTION" -> { navigateTo(ProductFormFragmentDirections
                        .actionProductFormFragmentToMyProductFragment())
                    }
                    "UPDATE" -> { navigateTo(ProductFormFragmentDirections
                        .actionProductFormFragmentToMyProductFragment())
                    }
                    "DELETION" -> { navigateTo(ProductFormFragmentDirections
                        .actionProductFormFragmentToMyProductFragment())
                    }
                    "NONE" -> { navigateTo(ProductFormFragmentDirections
                        .actionProductFormFragmentToMyProductFragment())
                    }
                    else -> { }
                }
            }
        }

        val spinner: Spinner = view.findViewById(R.id.quantitaMisura)

        class SpinnerAction: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                spinner.setSelection(pos)
                Log.d("Insert", spinner.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

                spinner.setSelection(0)
            }
        }

        // Imposta i valori nello spinner, l'elemento "-" corrisponde a nessuna unita di misura
      /* activity?.let {
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
        }*/

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Mostra la data di scadenza selezionata
        binding.dataButton.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _, year, tempMonth, tempDay ->

                // Mostra data selezionata nella TextView
                val monthOfYear =
                    if (month <= 9)
                        (tempMonth + 1).extendToTens()
                    else
                        (tempMonth + 1).toString()

                val dayOfMonth =
                    if (day <= 9)
                        tempDay.extendToTens()
                    else
                        tempDay.toString()

                binding.dataScadenza.text =
                    "$dayOfMonth/$monthOfYear/$year"
                binding.checkBoxSenzascadenza.isChecked = false
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        //Rimuove la data inserita se si spunta la checkbox di un prodotto senza scadenza
        binding.checkBoxSenzascadenza.setOnClickListener {
            binding.dataScadenza.text = "--/--/----"
        }

    }
}

