package it.project.houseoftasty.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import it.project.houseoftasty.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductNetwork : FirebaseNetwork("users") {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getProductByUser(): MutableList<Product> {

        lateinit var productList: MutableList<Product>
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = firestoreReference.document(firebaseAuth.uid.toString()).collection("products").get().await().documents
        }

        // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeList"
        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            productList = mutableListOf()

            for (document in documents) {
                Log.d("TAG", document.data.toString())
                val data = document.data
                val product = Product(document.id, data!!["nome"].toString(), data!!["quantita"].toString(), data!!["misura"].toString(), data!!["scadenza"].toString())
                productList.add(product)
            }
        }

        return productList
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    suspend fun getProductById(productId: String): Product {
        lateinit var product : Product
        lateinit var document : DocumentSnapshot

        // Recupera i dati sul prodotto (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = firestoreReference.document(currentUserId!!).collection("products").document(productId).get().await()
            Log.d("TAG","XD "+document.toObject(Product::class.java).toString())
        }

        Log.d("TAG",productId)

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
            firestoreReference.document(firebaseAuth.uid.toString()).collection("products").
                add(product).await()
        }
    }

    suspend fun updateProduct(product: Product) {
        withContext(Dispatchers.IO) {
            firestoreReference.document(firebaseAuth.uid.toString()).collection("products").
                document(product.id!!).set(product).await()
        }
    }

    suspend fun deleteProductById(productId: String) {
        withContext(Dispatchers.IO) {
            firestoreReference.document(firebaseAuth.uid.toString()).collection("products").
                document(productId).delete().await()
        }
    }
}
