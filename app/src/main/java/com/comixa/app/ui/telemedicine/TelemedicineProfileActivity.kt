package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.comixa.app.auth.AuthManager
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONObject

class TelemedicineProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleProfileToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tvName = findViewById(R.id.tvTeleProfileName)
        tvEmail = findViewById(R.id.tvTeleProfileEmail)
        tvPhone = findViewById(R.id.tvTeleProfilePhone)
        tvLocation = findViewById(R.id.tvTeleProfileLocation)

        val navHome = findViewById<LinearLayout>(R.id.navTeleHome)
        val navNotification = findViewById<LinearLayout>(R.id.navTeleNotification)
        val navPlus = findViewById<LinearLayout>(R.id.navTelePlus)
        val navSchedule = findViewById<LinearLayout>(R.id.navTeleSchedule)
        val navProfile = findViewById<LinearLayout>(R.id.navTeleProfile)

        navHome.setOnClickListener {
            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
        }
        navNotification.setOnClickListener {
            startActivity(Intent(this, TelemedicineApprovedActivity::class.java))
        }
        navPlus.setOnClickListener {
            startActivity(Intent(this, TelemedicineDoctorListActivity::class.java))
        }
        navSchedule.setOnClickListener {
            startActivity(Intent(this, TelemedicineScheduleActivity::class.java))
        }
        navProfile.setOnClickListener { /* уже тут */ }

        loadProfile()
    }

    private fun loadProfile() {
        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нужно войти в систему", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            return
        }

        Thread {
            val (code, responseText) = HttpHelper.get(ApiConfig.PROFILE_URL, token)

            runOnUiThread {
                if (code == 200) {
                    try {
                        val root = JSONObject(responseText)

                        // Если сервер заворачивает в { Status, Data }, берём Data
                        val data = root.optJSONObject("Data") ?: root

                        val name = data.optString(
                            "Name",
                            data.optString("fullName", "Unknown")
                        )
                        val email = data.optString(
                            "Email",
                            data.optString("email", "")
                        )
                        val phone = data.optString(
                            "Phone",
                            data.optString("phone", "")
                        )
                        val location = data.optString(
                            "Location",
                            data.optString("address", "")
                        )

                        tvName.text = name
                        tvEmail.text = email
                        tvPhone.text = phone
                        tvLocation.text = location

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "Ошибка обработки профиля: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка профиля: $code\n$responseText",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}
