package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R

class TelemedicineWelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_welcome)

        val btnSignUp = findViewById<Button>(R.id.btnTeleSignUp)
        val btnLogin = findViewById<Button>(R.id.btnTeleLogin)
        val tvUrgent = findViewById<TextView>(R.id.tvTeleUrgent)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, TelemedicineRegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
        }

        tvUrgent.setOnClickListener {
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
        }
    }
}
