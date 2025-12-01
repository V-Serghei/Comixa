package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.comixa.app.auth.AuthManager
import com.comixa.app.config.ApiConfig
import com.comixa.app.network.HttpHelper
import org.json.JSONObject

class TelemedicineLoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_login)

        etEmail = findViewById(R.id.etTeleEmail)
        etPassword = findViewById(R.id.etTelePassword)
        btnLogin = findViewById(R.id.btnTeleLoginSubmit)
        tvSignup = findViewById(R.id.tvTeleGoToSignup)

        btnLogin.setOnClickListener { doLogin() }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, TelemedicineRegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            val (code, responseText) = HttpHelper.postForm(
                urlString = ApiConfig.LOGIN_URL,
                params = mapOf(
                    "Email" to email,
                    "email" to email,
                    "Username" to email,
                    "username" to email,
                    "Password" to password,
                    "password" to password
                )
            )

            runOnUiThread {
                try {
                    if (code == 200) {
                        val obj = JSONObject(responseText)

                        val status = obj.optString("Status", "")
                        val msgToken = obj.optString("Message", "")

                        val jwtToken = obj.optString("token", "")

                        val token = when {
                            jwtToken.isNotEmpty() -> jwtToken
                            status.equals("SUCCESS", true) && msgToken.isNotEmpty() -> msgToken
                            else -> ""
                        }

                        if (token.isNotEmpty()) {
                            AuthManager.saveToken(this, token)
                            Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Сервер не вернул токен: $responseText",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Ошибка логина: $code\n$responseText",
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
