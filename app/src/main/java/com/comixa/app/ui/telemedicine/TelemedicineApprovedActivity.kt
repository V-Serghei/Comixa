package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.google.android.material.appbar.MaterialToolbar

class TelemedicineApprovedActivity : AppCompatActivity() {

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

        btnConfirm.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        navHome.setOnClickListener {
            startActivity(Intent(this, TelemedicineHomeActivity::class.java))
        }
        navNotification.setOnClickListener {  }
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
