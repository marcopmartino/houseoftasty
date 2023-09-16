package it.project.houseoftasty

import android.annotation.SuppressLint
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*

/**
  * Questa classe implementa un Worker che, ad intervalli regolari di 24h, controlla le scadenze dei prodotti
  * salvati nel database Room. In caso di scadenza prossima (2 giorni o meno), viene inviata
  * una notifica.
 **/
class ExpireWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams){


    /*private lateinit var currentDate: Date
    private val channelId = "checkExpire"
    private lateinit var builder: NotificationCompat.Builder*/

    override fun doWork(): Result {

       // checkDate()

        return Result.success()
    }

    /**
     * Questa funzione controlla le scadenze dei prodotti e, se necessario, invia una notifica
    **/
    /*@SuppressLint("SimpleDateFormat", "MissingPermission")
    private fun checkDate(){

        var counter = 0
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val temp = sdf.format(System.currentTimeMillis())
        currentDate = Date(temp)

        val products = houseTastyDao.getAll()
        for(product in products) {
            if(product.scadenza == "-") continue
            val chkDate = sdf.parse(product.scadenza)
            val diff = chkDate!!.time - currentDate.time
            val days = (((diff / 1000) / 60) / 60) / 24
            if (days <= 2) {
                counter++
            }
        }

        if(counter!=0) {

            builder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.medium_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            if (counter == 1) {
                builder.setContentTitle("Prodotto in scadenza")
                    .setContentText("Un prodotto è in scadenza")
            } else if (counter > 1) {
                builder.setContentTitle("Prodotto in scadenza")
                    .setContentText("Alcuni dei tuoi prodotti sono in scadenza")
            }

            with(NotificationManagerCompat.from(applicationContext)) {
                // notificationId is a unique int for each notification that you must define
                notify(410, builder.build())
            }

        }
    }*/

}