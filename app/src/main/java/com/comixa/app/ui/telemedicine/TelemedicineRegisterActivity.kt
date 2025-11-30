package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.comixa.app.R
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.app.AppCompatActivity

class TelemedicineRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_register)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleToolbarRegister)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnNext = findViewById<Button>(R.id.btnTeleNext)
        btnNext.setOnClickListener {
            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
        }
    }
}
