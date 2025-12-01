package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.comixa.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TelemedicineSplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_splash)

        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@TelemedicineSplashActivity, TelemedicineWelcomeActivity::class.java))
            finish()
        }
    }
}
