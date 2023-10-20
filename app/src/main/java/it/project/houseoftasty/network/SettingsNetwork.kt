package it.project.houseoftasty.network

import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsNetwork {
    private val profileNetwork = ProfileNetwork.getDataSource()
    private val productNetwork = ProductNetwork.getDataSource()
    private val recipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    private val deviceUserReference = FirebaseFirestore.getInstance()
        .collection("users")
        .document(profileNetwork.getAndroidId())

    suspend fun resetAccount() {
        recipeCollectionNetwork.deleteAllRecipes()
        recipeCollectionNetwork.deleteAllCollections()
        productNetwork.deleteAllProducts()
    }

    suspend fun deleteAccount() {
        resetAccount()
        profileNetwork.deleteUser()
    }

    /* Factory method */
    companion object {
        private var INSTANCE: SettingsNetwork? = null
        private var NEW_INSTANCE = SettingsNetwork()

        fun getDataSource(newInstance: Boolean = true): SettingsNetwork {
            return synchronized(SettingsNetwork::class) {
                if (newInstance) NEW_INSTANCE
                else {
                    val instance = INSTANCE ?: NEW_INSTANCE
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}