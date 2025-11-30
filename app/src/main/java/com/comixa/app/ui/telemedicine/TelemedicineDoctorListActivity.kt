package com.comixa.app.ui.telemedicine

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.comixa.app.R
import com.google.android.material.appbar.MaterialToolbar

class TelemedicineDoctorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tele_doctor_list)

        val toolbar = findViewById<MaterialToolbar>(R.id.teleDoctorListToolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val doctor1 = findViewById<LinearLayout>(R.id.doctorItem1)
        val doctor2 = findViewById<LinearLayout>(R.id.doctorItem2)
        val doctor3 = findViewById<LinearLayout>(R.id.doctorItem3)
        val doctor4 = findViewById<LinearLayout>(R.id.doctorItem4)
        val doctor5 = findViewById<LinearLayout>(R.id.doctorItem5)

        val openDetails: () -> Unit = {
            startActivity(Intent(this, TelemedicineDoctorDetailsActivity::class.java))
        }

        doctor1.setOnClickListener { openDetails() }
        doctor2.setOnClickListener { openDetails() }
        doctor3.setOnClickListener { openDetails() }
        doctor4.setOnClickListener { openDetails() }
        doctor5.setOnClickListener { openDetails() }

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
        navPlus.setOnClickListener { }
        navSchedule.setOnClickListener {
            startActivity(Intent(this, TelemedicineScheduleActivity::class.java))
        }
        navProfile.setOnClickListener {
            startActivity(Intent(this, TelemedicineProfileActivity::class.java))
        }
    }
}

