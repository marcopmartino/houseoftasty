package it.project.houseoftasty.repository

import android.util.Log
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.network.ProductNetwork

/* Handles operations on flowersLiveData and holds details about it. */
class ProductRepository {

    private val productNetwork = ProductNetwork()

    suspend fun getProductList(): MutableList<Product> {
        return productNetwork.getProductByUser()
    }

    suspend fun getProductById(productId: String): Product {
        return productNetwork.getProductById(productId)
    }

    suspend fun addProduct(product: Product) {
        return productNetwork.addProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        return productNetwork.updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) {
        return productNetwork.deleteProductById(productId)
    }

    /* Factory method */
    companion object {
        private var INSTANCE: ProductRepository? = null

        fun getDataSource(): ProductRepository {
            return synchronized(ProductRepository::class) {
                val newInstance = INSTANCE ?: ProductRepository()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}