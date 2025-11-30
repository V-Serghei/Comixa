package com.comixa.app.ui.labs.lab5

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.comixa.app.R
import com.comixa.app.ui.telemedicine.TelemedicineSplashActivity

class Lab5TelemedicineFragment : Fragment(R.layout.fragment_lab5_telemedicine) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn = view.findViewById<Button>(R.id.btnOpenTelemedicine)
        btn.setOnClickListener {
            startActivity(Intent(requireContext(), TelemedicineSplashActivity::class.java))
        }
    }
}
