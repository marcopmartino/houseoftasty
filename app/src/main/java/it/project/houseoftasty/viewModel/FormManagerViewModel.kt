package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.utility.OperationType
import it.project.houseoftasty.utility.update
import it.project.houseoftasty.validation.FormManager

/* ViewModel pensato per facilitare l'utilizzo della validazione delle form. Il metodo
* "generateValidatorBuilder" infatti consente di personalizzare il ValidatorBuilder (aggiungere
* campi da validare, regole di validazione ecc.) e di ultimarne la costruzione nella View, mentre
* la gestione al momento della pressione sul pulsante di Submit o di eliminazione può essere
* definita nel ViewModel tramite override dei metodi "onFormSubmit" e "onFormDelete".
* Tipicamente il ViewModel andrà ad aggiornare il database.
* */
abstract class FormManagerViewModel(initialStatus: Boolean = false) : LoadingManagerViewModel(initialStatus) {
    val operationCompleted: MutableLiveData<OperationType> = MutableLiveData(OperationType.DEFAULT)

    /* Genera un ValidatorBuilder che gestisce in maniera predefinita l'interazione con la form:
    * se la validazione ha successo, allora indica che il caricamento è iniziato (può essere usato
    * per aggiornare la schermata automaticamente) ed esegue il metodo onFormSubmit; analogamente
    * per il pulsante di eliminazione (se presente).
    * */
    fun generateFormManagerBuilder(customHasDataChangedFunction: Boolean = true): FormManager.Builder {

        val builder: FormManager.Builder = FormManager.Builder()
            .onValidationSuccess { formData, hasDataChanged ->
                startLoadingAndDo { onFormSubmit(formData, hasDataChanged) } }
            .onDeleteButtonClicked { startLoadingAndDo { onFormDelete() } }

        if (customHasDataChangedFunction) builder.hasDataChanged { formData -> hasDataChanged(formData) }

        return builder
    }

    // Funzione da eseguire alla pressione del pulsante di Submit
    abstract fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean)

    // Funzione da eseguire alla pressione del pulsante di eliminazione
    open fun onFormDelete() { }

    // Controlla se i campi in input sono cambiati
    open fun hasDataChanged(formData: MutableMap<String, Any>): Boolean {
        return true
    }

    /* Funzione che può essere usata per indicare quale tipo di operazione è stata completata (in
    * genere si tratta di operazioni sul database). Osservando "operationCompleted" dalla View è
    * possibile gestire il termine delle operazioni eseguite dal ViewModel, per esempio
    * navigando verso un nuovo Fragment in seguito a un'operazione sul database.
     */
    fun setOperationCompleted(operationType: OperationType = OperationType.UNKNOWN) {
        operationCompleted.update(operationType)
    }

}