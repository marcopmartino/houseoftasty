package it.project.houseoftasty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences("it.project.houseoftasty", MODE_PRIVATE)

        if(!pref.contains("firstRun")){
            pref.edit().putBoolean("firstRun", false).apply()

            setContentView(R.layout.activity_welcome)
            findViewById<Button>(R.id.button_main_prosegui).setOnClickListener {
                val intent = Intent(this, AccessActivity::class.java)
                startActivity(intent)
            }
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}