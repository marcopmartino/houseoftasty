package it.project.houseoftasty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

//Schermata primo avvio
class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.button_main_prosegui).setOnClickListener {
            val intent = Intent(this, AccessActivity::class.java)
            startActivity(intent)
        }
    }
}