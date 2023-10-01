package it.project.houseoftasty.validation

import android.util.Log
import android.view.View
import android.widget.Button
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import it.project.houseoftasty.R
import it.project.houseoftasty.view.PERMISSION_REQUEST_CAMERA
import it.project.houseoftasty.view.PERMISSION_REQUEST_GALLERY

/* Classe per gestire la validazione e il submit di una Form. Ha come proprietà:
* editTextValidators: MutableList con i validatori associati ai campi EditText della form;
* imageViewValidators: MutableList con i validatori associati ai campi ImageView della form;
* unvalidatedFields: MutableList con i campi non validati;
* onValidationCompleted: funzione da eseguire successivamente alla validazione della form;
* onValidationSuccess: funzione da eseguire se la validazione della form ha successo;
* onValidationFailure: funzione da eseguire se la validazione della form non ha successo;
* onDeleteButtonClicked: funzione da eseguire al click sul pulsante di eliminazione, o eventualmente
*   alla conferma dell'eliminazione tramite Dialog;
* deleteConfirmationDialog: Dialog da mostrare al click sul pulsante di eliminazione;
* hasDataChanged: funzione per determinare se i dati in input sono cambiati.
*
* La classe viene costruita facendo uso del pattern Builder */
class FormManager {
    private var editTextValidators: MutableList<EditTextValidator> = mutableListOf()
    private var imageViewValidators: MutableList<ImageViewValidator> = mutableListOf()
    private var unvalidatedFields: MutableList<InputField<View>> = mutableListOf()
    private var onValidationCompleted: (formData: MutableMap<String, Any>, hasDataChanged: Boolean)
        -> Unit = { _: MutableMap<String, Any>, _: Boolean -> }
    private var onValidationSuccess: (formData: MutableMap<String, Any>, hasDataChanged: Boolean)
        -> Unit = { _: MutableMap<String, Any>, _: Boolean -> }
    private var onValidationFailure: () -> Unit = {}
    private var onDeleteButtonClicked: () -> Unit = {}
    private var deleteConfirmationDialog: AlertDialog? = null
    private var hasDataChanged: (formData: MutableMap<String, Any>) -> Boolean = { true }

    // Eseguita al click sul pulsante di eliminazione
    private fun onDelete() {
        if (deleteConfirmationDialog == null)
            onDeleteButtonClicked.invoke()
        else
            deleteConfirmationDialog!!.show()
    }

    // Eseguita al click sul pulsante di Submit
    private fun onSubmit() {
        val success = validateAll()
        val formData = getAllInputData()
        val hasDataChanged = hasDataChanged.invoke(formData)
        onValidationCompleted.invoke(formData, hasDataChanged)
        if (success) onValidationSuccess.invoke(formData, hasDataChanged)
            else onValidationFailure.invoke()
    }

    /* Esegue la validazione per ciascun EditTextValidator presente nella lista "validators".
    *  Ritorna TRUE se tutte le validazioni sono andate a buon fine, altrimenti FALSE. */
    private fun validateAll(): Boolean {
        var isValid = true
        for (validator in editTextValidators) {
            if (!validator.validate())
                isValid = false
        }
        if (isValid)
            for (validator in imageViewValidators) {
                if (!validator.validate())
                    isValid = false
            }
        return isValid
    }

    // Crea una mappa con tutti i dati inseriti nella form
    private fun getAllInputData(): MutableMap<String, Any> {
        val dataMap = mutableMapOf<String,Any>()
        for (validator in editTextValidators)
            dataMap[validator.getInputViewTag()] = validator.getInputData()
        for (validator in imageViewValidators) {
            dataMap[validator.getInputViewTag()] = validator.getInputData()
            dataMap[validator.getInputViewTag() + "OperationType"] = validator.getLastOperation()
        }
        for (field in unvalidatedFields)
            dataMap[field.getInputViewTag()] = field.getInputData()
        return dataMap
    }

    /* Builder class per la classe FormManager
    * Questa classe inizializza un oggetto FormManager e lo costruisce per passi attraverso metodi
    * che ritornano un'istanza della classe Builder stessa. Il metodo build() va eseguito al termine
    * della costruzione e ritorna l'oggetto FormManager personalizzato.
    *
    * Per maggiore flessibilità e comodità, questa classe Builder può lavorare sia con validatori
    * già costruiti (oggetti EditTextValidator), sia con validatori "semi-costruiti" (oggetti
    * EditTextValidator.Builder), anche contemporaneamente: in questo modo è possibile specificare una volta
    * sola le regole di validazione e altre opzioni che sono comuni a tutti i validatori
    * semi-costruiti, risparmiando righe di codice.
    * I validatori già costruiti non possono subire modifiche e pertanto non saranno influenzati
    * da modifiche alle proprietà dei validatori semi-costruiti.
    *
    * Questa classe Builder si occupa anche di ultimare la costruzione dei validatori semi-costruiti.
    *
    * Le proprietà della classe Builder sono usate solo durante la costruzione del validatore:
    * formManager: l'oggetto FormManager da costruire;
    * editTextValidatorBuilders: lista dei validatori "semi-costruiti" (oggetti EditTextValidator.Builder);
    * imageViewValidatorBuilders: lista dei validatori "semi-costruiti" (oggetti ImageViewValidator.Builder);
    * commonRules: lista delle regole di validazione comuni a tutti i validatori semi-costruiti;
    * prependCommonRules: indica se aggiungere le regole comuni a tutti i validatori semi-costruiti
    *   in cima o in fondo alla lista delle regole già definite per ognuno;
    * commonOnTextChangedValidation: indica se la validazione comune onTextChanged (per i validatori
    *   semi-costruiti che lo consentono) è abilitata;
    * commonOnFocusLostValidation: indica se la validazione comune onFocusLost (per i validatori
    *   semi-costruiti che lo consentono) è abilitata.
    *  */
    class Builder {
        private val formManager: FormManager = FormManager()
        private val editTextValidatorBuilders: MutableList<EditTextValidator.Builder> = mutableListOf()
        private val imageViewValidatorBuilders: MutableList<ImageViewValidator.Builder> = mutableListOf()
        private val commonRules: MutableList<ValidationRule> = mutableListOf()
        private var prependCommonRules: Boolean = true
        private var commonOnTextChangedValidation: Boolean = false
        private var commonOnFocusLostValidation: Boolean = false

        // Imposta un pulsante di Submit per il FormManager
        fun setSubmitButton(submitButton: Button): Builder {
            submitButton.setOnClickListener { formManager.onSubmit() }
            return this
        }

        // Imposta un pulsante di eliminazione per il FormManager
        fun setDeleteButton(deleteButton: Button): Builder {
            deleteButton.setOnClickListener { formManager.onDelete() }
            return this
        }

        // Imposta un Dialog per la conferma dell'eliminazione
        fun setDeleteConfirmationDialog(
            context: Context,
            message: String = "Sei sicuro di voler procedere con l'eliminazione?"): Builder {
            formManager.deleteConfirmationDialog =
                AlertDialog.Builder(context)
                    .setTitle("Conferma eliminazione")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Sì") { _, _ ->
                        // Esegue la funzione di eliminazione
                        formManager.onDeleteButtonClicked.invoke()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Rimuove il Dialog
                        dialog.dismiss()
                    }
                    .create()
            return this
        }

        // Aggiunge uno o più validatori per EditText alla lista dei validatori di FormManager
        fun addEditTextValidators(vararg validators: EditTextValidator): Builder {
            for (validator in validators)
                formManager.editTextValidators.add(validator)
            return this
        }

        /* Aggiunge uno o più validatori per EditText semi-costruiti alla lista
         "editTextValidatorBuilders" di questa classe */
        fun addEditTextValidators(vararg builders: EditTextValidator.Builder): Builder {
            for (builder in builders)
                editTextValidatorBuilders.add(builder)
            return this
        }

        // Aggiunge uno o più validatori per ImageView alla lista dei validatori di FormManager
        fun addImageViewValidators(vararg validators: ImageViewValidator): Builder {
            for (validator in validators)
                formManager.imageViewValidators.add(validator)
            return this
        }

        /* Aggiunge uno o più validatori per ImageView semi-costruiti alla lista
         "imageViewValidatorBuilders" di questa classe */
        fun addImageViewValidators(vararg builders: ImageViewValidator.Builder): Builder {
            for (builder in builders)
                imageViewValidatorBuilders.add(builder)
            return this
        }

        // Aggiunge uno o più campi non validati
        fun addUnvalidatedFields(vararg fields: View): Builder {
            for (field in fields)
                formManager.unvalidatedFields.add(InputField(field))
            return this
        }

        /* Aggiunge una o più regole di validazione alla lista delle regole comuni a tutti i
        * validatori semi-costruiti. */
        fun addCommonRules(vararg rules: ValidationRule): Builder {
            for (rule in rules)
                commonRules.add(rule)
            return this
        }

        /* Fa in modo che la validazione delle regole comuni venga eseguita prima della validazione
        * delle regole specifiche di ogni validatore. */
        fun checkCommonRulesFirst(check: Boolean = true) {
            prependCommonRules = check
        }

        /* Abilita\disabilita la validazione comune automatica ogni volta che il testo del campo di
        * input cambia */
        fun enableOnTextChangedValidation(enabled: Boolean = true): Builder {
            commonOnTextChangedValidation = enabled
            return this
        }

        /* Abilita\disabilita la validazione comune automatica ogni volta che il campo di input
        * perde il focus */
        fun enableOnFocusLostValidation(enabled: Boolean = true): Builder {
            commonOnFocusLostValidation = enabled
            return this
        }

        // Imposta la funzione da eseguire successivamente alla validazione della form
        fun onValidationCompleted(function: (formData: MutableMap<String, Any>,
                                             hasDataChanged: Boolean) -> Unit): Builder {
            formManager.onValidationCompleted = function
            return this
        }

        // Imposta la funzione da eseguire se la validazione della form ha successo
        fun onValidationSuccess(function: (formData: MutableMap<String, Any>,
                                           hasDataChanged: Boolean) -> Unit): Builder {
            formManager.onValidationSuccess = function
            return this
        }

        // Imposta la funzione da eseguire se la validazione della form non ha successo
        fun onValidationFailure(function: () -> Unit): Builder {
            formManager.onValidationFailure = function
            return this
        }

        // Imposta la funzione da eseguire al click sul pulsante di eliminazione
        fun onDeleteButtonClicked(function: () -> Unit): Builder {
            formManager.onDeleteButtonClicked = function
            return this
        }

        // Imposta una funzione personalizzata per controllare se i dati in input sono cambiati
        fun hasDataChanged(function: (formData: MutableMap<String, Any>) -> Boolean): Builder {
            formManager.hasDataChanged = function
            return this
        }

        // Ultima la costruzione del FormManager e lo ritorna
        fun build(): FormManager {
            for (builder in editTextValidatorBuilders) {
                for (rule in commonRules)
                    builder.addRules(rule, prepend = prependCommonRules)
                if (builder.allowExternalOptionChanges) {
                    builder.enableOnTextChangedValidation(commonOnTextChangedValidation)
                    builder.enableOnFocusLostValidation(commonOnFocusLostValidation)
                }
                formManager.editTextValidators.add(builder.build())
            }
            for (builder in imageViewValidatorBuilders)
                formManager.imageViewValidators.add(builder.build())
            return formManager
        }
    }

}