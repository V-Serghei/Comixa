package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.comixa.app.auth.AuthManager
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import org.json.JSONObject

class TelemedicineHomeActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDisease: EditText
    private lateinit var etLocation: EditText
    private lateinit var etDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_home)

        etName = findViewById(R.id.etTeleName)
        etDisease = findViewById(R.id.etTeleDesease)
        etLocation = findViewById(R.id.etTeleLocation)
        etDescription = findViewById(R.id.etTeleDescription)

        val btnVeryUrgent = findViewById<Button>(R.id.btnTeleVeryUrgent)
        val btnRequest = findViewById<Button>(R.id.btnTeleRequest)

        val navHome = findViewById<LinearLayout>(R.id.navTeleHome)
        val navNotification = findViewById<LinearLayout>(R.id.navTeleNotification)
        val navPlus = findViewById<LinearLayout>(R.id.navTelePlus)
        val navSchedule = findViewById<LinearLayout>(R.id.navTeleSchedule)
        val navProfile = findViewById<LinearLayout>(R.id.navTeleProfile)

        btnVeryUrgent.setOnClickListener { sendConsultation(isVeryUrgent = true) }
        btnRequest.setOnClickListener { sendConsultation(isVeryUrgent = false) }

        navHome.setOnClickListener { /* уже тут */ }
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
    }

    private fun sendConsultation(isVeryUrgent: Boolean) {
        val name = etName.text.toString().trim()
        val disease = etDisease.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val desc = etDescription.text.toString().trim()

        if (name.isEmpty() || disease.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Заполните имя, заболевание и локацию", Toast.LENGTH_SHORT).show()
            return
        }

        val token = AuthManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Нужно войти в систему", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TelemedicineLoginActivity::class.java))
            return
        }

        Thread {
            // ВАЖНО: имена полей такие же, как в ConsultationRequest на бэке
            val params = mapOf(
                "name" to name,
                "disease" to disease,
                "address" to location,          // вот это закрывает твою ошибку address must not be blank
                "description" to desc,
                "urgent" to isVeryUrgent.toString()
            )

            val (code, responseText) = HttpHelper.postForm(
                urlString = ApiConfig.CONSULTATION_URL,
                params = params,
                token = token
            )

            runOnUiThread {
                try {
                    if (code == 200) {
                        val obj = JSONObject(responseText)
                        val status = obj.optString("Status", "")
                        val consultationId =
                            obj.optInt("Message", obj.optInt("ConsultationId", -1))

                        if (status.equals("SUCCESS", ignoreCase = true) || status.isEmpty()) {
                            Toast.makeText(this, "Запрос отправлен", Toast.LENGTH_SHORT).show()

                            // Переключаемся на экран Approved и передаём данные
                            val intent = Intent(this, TelemedicineApprovedActivity::class.java).apply {
                                putExtra("name", name)
                                putExtra("disease", disease)
                                putExtra("location", location)
                                putExtra("desc", desc)
                                putExtra("consultation_id", consultationId)
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Ошибка отправки: $responseText",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Ошибка отправки: $code\n$responseText",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Ошибка обработки ответа: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}
