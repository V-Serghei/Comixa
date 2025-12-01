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
import org.json.JSONArray
import org.json.JSONObject

class TelemedicineScheduleActivity : AppCompatActivity() {

    private lateinit var tvSchedule: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_schedule)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleScheduleToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tvSchedule = findViewById(R.id.tvTeleSchedulePlaceholder)

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
        navSchedule.setOnClickListener { /* уже тут */ }
        navProfile.setOnClickListener {
            startActivity(Intent(this, TelemedicineProfileActivity::class.java))
        }

        loadConsultations()
    }

    private fun loadConsultations() {
        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нужно войти в систему", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            return
        }

        Thread {
            val (code, responseText) = HttpHelper.get(ApiConfig.CONSULTATIONS_ALL_URL, token)

            runOnUiThread {
                if (code == 200) {
                    try {
                        val arr = JSONArray(responseText)
                        val sb = StringBuilder()
                        for (i in 0 until arr.length()) {
                            val o: JSONObject = arr.getJSONObject(i)
                            val doctorName = o.optString("DoctorName", "Unknown doctor")
                            val date = o.optString("Date", "")
                            val status = o.optString("Status", "")

                            sb.append("${i + 1}. $date - $doctorName ($status)\n")
                        }
                        tvSchedule.text =
                            if (sb.isNotEmpty()) sb.toString() else "Нет консультаций"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "Ошибка парсинга консультаций: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка загрузки консультаций: $code\n$responseText",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}
