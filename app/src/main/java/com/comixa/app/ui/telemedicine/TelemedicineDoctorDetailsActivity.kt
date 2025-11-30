package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.google.android.material.appbar.MaterialToolbar

class TelemedicineDoctorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_doctor_details)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleDoctorDetailsToolbar)
        toolbar.setNavigationOnClickListener { finish() }

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
    }
}

