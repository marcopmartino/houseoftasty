package it.project.houseoftasty.viewModel

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.project.houseoftasty.utility.OperationType
import it.project.houseoftasty.utility.update
import kotlinx.coroutines.launch

open class LoadingManagerViewModel(initialStatus: Boolean = false) : ViewModel() {
    val initializationCompleted: MutableLiveData<Boolean> = MutableLiveData(initialStatus)
    val loadingCompleted: MutableLiveData<Boolean> = MutableLiveData(initialStatus)

    protected open fun initSync() { }
    protected open suspend fun initAsync() { }

    fun initialize() {
        initSync()
        viewModelScope.launch {
            initAsync()
            stopInitializing()
        }
    }

    fun reinitialize() {
        startInitializing()
        initialize()
    }

    private fun startInitializing() {
        setInitializationCompleted(false)
        startLoading()
    }

    private fun stopInitializing() {
        setInitializationCompleted(true)
        stopLoading()
    }

    fun startLoading() {
        setLoadingCompleted(false)
    }

    fun stopLoading() {
        setLoadingCompleted(true)
    }

    fun startLoadingAndDo(loadingFunction: () -> Unit) {
        startLoading()
        loadingFunction.invoke()
    }

    fun whileLoading(loadingFunction: () -> Unit) {
        startLoading()
        loadingFunction.invoke()
        stopLoading()
    }

    private fun setInitializationCompleted(isCompleted: Boolean = true) {
        initializationCompleted.update(isCompleted)
    }

    private fun setLoadingCompleted(isCompleted: Boolean = true) {
        loadingCompleted.update(isCompleted)
    }

}