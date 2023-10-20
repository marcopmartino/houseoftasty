package it.project.houseoftasty.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import it.project.houseoftasty.R
import kotlin.reflect.KClass


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun startActivity(activityClass: KClass<*>) {
            startActivity(Intent(this, activityClass.java))
        }

        fun startWelcomeActivity() {
            // Mostro il layout della WelcomeActivity
            setContentView(R.layout.activity_welcome)

            // Listener sul pulsante "Prosegui" per navigare verso l'AccessActivity
            findViewById<Button>(R.id.button_main_prosegui).setOnClickListener {
                startActivity(AccessActivity::class)
            }
        }

        val pref = getSharedPreferences("it.project.houseoftasty", MODE_PRIVATE)

        // Controllo se si tratta del primo avvio
        if (pref.contains("firstRun")) {
            // Avvio un'activity in base alle impostazioni di avvio
            when (pref.getString("startActivity", "Home")) {
                "Home" -> startActivity(MainActivity::class)
                "Access" -> startActivity(AccessActivity::class)
                "Welcome" -> startWelcomeActivity()
                else -> Log.d("startActivity", pref.getString("startActivity", "").toString())
            }
        } else {
            // Aggiorno la preferenza sul primo avvio
            pref.edit().putBoolean("firstRun", false).apply()

            startWelcomeActivity()
        }

    }
}