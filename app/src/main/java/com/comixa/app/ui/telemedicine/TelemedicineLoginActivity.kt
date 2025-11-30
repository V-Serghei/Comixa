package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R

class TelemedicineLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_login)

        val btnLogin = findViewById<Button>(R.id.btnTeleLoginSubmit)
        val tvSignup = findViewById<TextView>(R.id.tvTeleGoToSignup)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, TelemedicineRegisterActivity::class.java))
        }
    }
}
