package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comixa.app.R
import com.comixa.app.auth.AuthManager
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject

data class TeleDoctorUi(
    val id: Int,
    val name: String,
    val speciality: String,
    val rating: String
)

class TelemedicineDoctorListActivity : AppCompatActivity() {

    private lateinit var adapter: TeleDoctorAdapter
    private val items = mutableListOf<TeleDoctorUi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_doctor_list)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleDoctorListToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvTeleDoctors)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = TeleDoctorAdapter(items) { doctor ->
            val i = Intent(this, TelemedicineDoctorDetailsActivity::class.java)
            i.putExtra("doctorId", doctor.id)                // ВАЖНО: doctorId
            i.putExtra("doctorName", doctor.name)
            i.putExtra("doctorSpeciality", doctor.speciality)
            i.putExtra("doctorRating", doctor.rating)
            startActivity(i)
        }
        rv.adapter = adapter

        setupBottomNav()
        loadDoctorsFromApi()
    }

    private fun setupBottomNav() {
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
        navPlus.setOnClickListener { /* уже здесь */ }
        navSchedule.setOnClickListener {
            startActivity(Intent(this, TelemedicineScheduleActivity::class.java))
        }
        navProfile.setOnClickListener {
            startActivity(Intent(this, TelemedicineProfileActivity::class.java))
        }
    }

    private fun loadDoctorsFromApi() {
        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нет токена, сначала войдите", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            finish()
            return
        }

        Thread {
            try {
                val (code, body) = HttpHelper.authedGet(ApiConfig.DOCTOR_LIST_URL, token)

                runOnUiThread {
                    if (code == 200) {
                        try {
                            val trimmed = body.trim()
                            val arr: JSONArray = if (trimmed.startsWith("[")) {
                                JSONArray(trimmed)
                            } else {
                                val root = JSONObject(trimmed)
                                when {
                                    root.has("Data") -> root.getJSONArray("Data")
                                    root.has("data") -> root.getJSONArray("data")
                                    else -> throw IllegalStateException(
                                        "Неизвестный формат ответа докторов: $trimmed"
                                    )
                                }
                            }

                            items.clear()
                            for (i in 0 until arr.length()) {
                                val obj = arr.getJSONObject(i)

                                val id = obj.optInt("id", obj.optInt("DocId", 0))
                                val name = obj.optString(
                                    "fullName",
                                    obj.optString("Name", "Unknown Doctor")
                                )
                                val spec = obj.optString(
                                    "specs",
                                    obj.optString("Speciality", "Specialist")
                                )
                                val ratingValue = obj.optDouble(
                                    "stars",
                                    obj.optDouble("Rating", 0.0)
                                )

                                items.add(
                                    TeleDoctorUi(
                                        id = id,
                                        name = name,
                                        speciality = spec,
                                        rating = "★ ${"%.1f".format(ratingValue)}"
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Parse doctors error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Doctor list error: $code\n$body",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Network error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}
