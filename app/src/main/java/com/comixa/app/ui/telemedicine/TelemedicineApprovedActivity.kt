package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.comixa.app.auth.AuthManager
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import com.google.android.material.appbar.MaterialToolbar

class TelemedicineApprovedActivity : AppCompatActivity() {

    private var consultationId: Int = -1

    private lateinit var tvName: TextView
    private lateinit var tvDisease: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_approved)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleApprovedToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val btnConfirm = findViewById<Button>(R.id.btnTeleConfirm)
        val btnCancel = findViewById<Button>(R.id.btnTeleCancel)

        val navHome = findViewById<LinearLayout>(R.id.navTeleHome)
        val navNotification = findViewById<LinearLayout>(R.id.navTeleNotification)
        val navPlus = findViewById<LinearLayout>(R.id.navTelePlus)
        val navSchedule = findViewById<LinearLayout>(R.id.navTeleSchedule)
        val navProfile = findViewById<LinearLayout>(R.id.navTeleProfile)

        tvName = findViewById(R.id.tvApprovedName)
        tvDisease = findViewById(R.id.tvApprovedDisease)
        tvLocation = findViewById(R.id.tvApprovedLocation)
        tvDesc = findViewById(R.id.tvApprovedDescription)

        tvName.text = intent.getStringExtra("name") ?: "—"
        tvDisease.text = intent.getStringExtra("disease") ?: "—"
        tvLocation.text = intent.getStringExtra("location") ?: "—"
        tvDesc.text = intent.getStringExtra("desc") ?: "—"

        consultationId = intent.getIntExtra("consultation_id", -1)

        btnConfirm.setOnClickListener {
            updateConsultationStatus("APPROVED")
        }
        btnCancel.setOnClickListener {
            updateConsultationStatus("CANCELLED")
        }

        navHome.setOnClickListener {
            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
        }
        navNotification.setOnClickListener { /* уже здесь */ }
        navPlus.setOnClickListener {
            startActivity(Intent(this, TelemedicineDoctorListActivity::class.java))
        }
        navSchedule.setOnClickListener {
            startActivity(Intent(this, TelemedicineScheduleActivity::class.java))
        }
        navProfile.setOnClickListener {
            startActivity(Intent(this, TelemedicineProfileActivity::class.java))
        }
    }

    private fun updateConsultationStatus(status: String) {
        if (consultationId <= 0) {
            finish()
            return
        }

        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нужно войти в систему", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            return
        }

        Thread {
            val url = ApiConfig.consultationUpdateUrl(consultationId, status)
            val (code, responseText) = HttpHelper.get(url, token)

            runOnUiThread {
                if (code == 200) {
                    Toast.makeText(this, "Статус обновлён: $status", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка обновления: $code\n$responseText",
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }.start()
    }
}
