package it.project.houseoftasty.network

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

open class StorageNetwork(initialStoragePath: String) {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(initialStoragePath)

    open suspend fun getFileReference(path: String): StorageReference? {
        return storageReference.child(path)
    }

    open suspend fun deleteFile(path: String) {
        withContext(Dispatchers.IO) {
            storageReference.child(path).delete().await()
        }
    }

    open suspend fun uploadFile(path: String, file: File) {
        withContext(Dispatchers.IO) {
            storageReference.child(path).putFile(Uri.fromFile(file)).await()
        }
    }

    open suspend fun uploadFileFromByteArray(path: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            storageReference.child(path).putBytes(data).await()
        }
    }

    open suspend fun uploadFileFromStream(storagePath: String, filePath: String) {
        withContext(Dispatchers.IO) {
            storageReference.child(storagePath).putStream(FileInputStream(File(filePath))).await()
        }
    }


}