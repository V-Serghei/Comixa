package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R

class TelemedicineHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_home)

        val btnVeryUrgent = findViewById<Button>(R.id.btnTeleVeryUrgent)
        val btnRequest = findViewById<Button>(R.id.btnTeleRequest)

        val navHome = findViewById<LinearLayout>(R.id.navTeleHome)
        val navNotification = findViewById<LinearLayout>(R.id.navTeleNotification)
        val navPlus = findViewById<LinearLayout>(R.id.navTelePlus)
        val navSchedule = findViewById<LinearLayout>(R.id.navTeleSchedule)
        val navProfile = findViewById<LinearLayout>(R.id.navTeleProfile)

        val openApproved = {
            startActivity(Intent(this, TelemedicineApprovedActivity::class.java))
        }

        btnVeryUrgent.setOnClickListener { openApproved() }
        btnRequest.setOnClickListener { openApproved() }

        navHome.setOnClickListener {  }
        navNotification.setOnClickListener { openApproved() }
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
