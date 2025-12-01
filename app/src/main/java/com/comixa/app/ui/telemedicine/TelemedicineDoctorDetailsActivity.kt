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
import org.json.JSONObject

class TelemedicineDoctorDetailsActivity : AppCompatActivity() {

    private var doctorId: Int = -1

    private lateinit var tvName: TextView
    private lateinit var tvSpec: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_doctor_details)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleDoctorDetailsToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tvName = findViewById(R.id.tvDoctorName)
        tvSpec = findViewById(R.id.tvDoctorSpeciality)
        tvLocation = findViewById(R.id.tvDoctorLocation)
        tvRating = findViewById(R.id.tvDoctorRating)
        tvDesc = findViewById(R.id.tvDoctorShortDesc)

        val btnRequest = findViewById<Button>(R.id.btnTeleDoctorRequest)
        btnRequest.setOnClickListener {
            startActivity(Intent(this, TelemedicineApprovedActivity::class.java))
        }

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
        navProfile.setOnClickListener {
            startActivity(Intent(this, TelemedicineProfileActivity::class.java))
        }

        doctorId = intent.getIntExtra("doctorId", -1)
        if (doctorId > 0) {
            loadDoctor()
        } else {
            Toast.makeText(this, "Доктор не выбран", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDoctor() {
        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нужно войти в систему", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            return
        }

        Thread {
            val url = ApiConfig.doctorUrl(doctorId)
            val (code, responseText) = HttpHelper.get(url, token)

            runOnUiThread {
                if (code == 200) {
                    try {
                        val o = JSONObject(responseText)

                        val name = o.optString(
                            "fullName",
                            o.optString("Name", o.optString("FullName", "Unknown doctor"))
                        )
                        val spec = o.optString(
                            "specs",
                            o.optString("Speciality", o.optString("Specs", "Unknown"))
                        )
                        val loc = o.optString(
                            "address",
                            o.optString("Location", o.optString("Address", "Unknown address"))
                        )
                        val rating = o.optDouble(
                            "stars",
                            o.optDouble("Rating", o.optDouble("Stars", 0.0))
                        )
                        val desc = o.optString(
                            "about",
                            o.optString("Description", o.optString("About", "No description"))
                        )

                        tvName.text = name
                        tvSpec.text = spec
                        tvLocation.text = loc
                        tvRating.text =
                            if (rating > 0) "★ ${"%.1f".format(rating)}" else "-"
                        tvDesc.text = desc

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "Ошибка данных доктора: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка загрузки доктора: $code\n$responseText",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}
