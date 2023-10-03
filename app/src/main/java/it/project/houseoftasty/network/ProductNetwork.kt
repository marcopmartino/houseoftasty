package it.project.houseoftasty.network

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProductNetwork {

    private val currentUserId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private val productsReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
            .collection("products")

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getProductByUser(): MutableList<Product> {

        lateinit var productList: MutableList<Product>
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sui prodotti (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = productsReference.get().await().documents
        }

        // Converte gli snapshot dei documenti in oggetti Product e li aggiunge alla lista "productList"
        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            productList = mutableListOf()

            for (document in documents) {
                val data = document.data
                val product = Product(
                    document.id,
                    data!!["nome"].toString(),
                    data["quantita"].toString(),
                    data["misura"].toString(),
                    data["scadenza"].toString())
                productList.add(product)
            }
        }

        return productList
    }

    // Ritorna tutti i dati sul prodotto di cui è noto l'id
    suspend fun getProductById(productId: String): Product {
        lateinit var product : Product
        lateinit var document : DocumentSnapshot

        // Recupera i dati sul prodotto (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = productsReference.document(productId).get().await()
        }

        // Converte lo snapshot del documento in un oggetto Product
        document.toObject(Product::class.java).also {
            if (it != null) {
                product = it
            }
        }

        return product
    }

    suspend fun addProduct(product: Product) {

        withContext(Dispatchers.IO) {
            productsReference.add(product).await()
        }
    }

    suspend fun updateProduct(product: Product) {
        withContext(Dispatchers.IO) {
            productsReference.document(product.id!!).set(product).await()
        }
    }

    suspend fun deleteProductById(productId: String) {
        withContext(Dispatchers.IO) {
            productsReference.document(productId).delete().await()
        }
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun getExpireProduct(): MutableList<Product>{
        val products = getProductByUser()
        val expire: MutableList<Product> = mutableListOf()

        val sdf = SimpleDateFormat("dd/MM/yyyy")

        for(product in products){
            val dataScadenza: String? = product.scadenza

            if (dataScadenza != "--/--/----") {

                val chkDate = sdf.parse(dataScadenza!!)
                val diff = chkDate!!.time - Date().time
                val hours = (((diff / 1000) / 60) / 60)

                Log.d("Ore di differenza", hours.toString())

                if (hours in -23..48) {
                    continue
                } else if (hours<=-24) {
                    expire.add(product)
                }
            }
        }
        return expire
    }

    /* Factory method */
    companion object {
        private var INSTANCE: ProductNetwork? = null

        fun getDataSource(): ProductNetwork {
            return synchronized(ProductNetwork::class) {
                val newInstance = INSTANCE ?: ProductNetwork()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}
