package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONObject

class TelemedicineRegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etBirthday: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_register)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleToolbarRegister)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        etFullName = findViewById(R.id.etTeleFullName)
        etBirthday = findViewById(R.id.etTeleBirthday)
        etEmail = findViewById(R.id.etTeleEmail)
        etPhone = findViewById(R.id.etTelePhone)
        etLocation = findViewById(R.id.etTeleLocation)
        btnNext = findViewById(R.id.btnTeleNext)

        btnNext.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val fullName = etFullName.text.toString().trim()
        val birthday = etBirthday.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etLocation.text.toString().trim()

        if (fullName.isEmpty() || birthday.isEmpty() ||
            email.isEmpty() || phone.isEmpty() || address.isEmpty()
        ) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val username = email
        val password = phone

        Thread {
            val (code, responseText) = HttpHelper.postForm(
                urlString = ApiConfig.REGISTER_URL,
                params = mapOf(
                    "fullName" to fullName,
                    "birthday" to birthday,
                    "email" to email,
                    "phone" to phone,
                    "address" to address,
                    "username" to username,
                    "password" to password
                )
            )

            runOnUiThread {
                try {
                    if (code == 200 || code == 201) {
                        try {
                            val obj = JSONObject(responseText)
                            val status = obj.optString("Status", "")
                            val message = obj.optString("Message", "")
                            if (status.isNotEmpty() || message.isNotEmpty()) {
                                Toast.makeText(
                                    this,
                                    "Регистрация: $status $message",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Регистрация успешна",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (_: Exception) {
                            Toast.makeText(
                                this,
                                "Регистрация успешна",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        startActivity(Intent(this, TelemedicineLoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Ошибка регистрации: $code\n$responseText",
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
