package com.comixa.app.config

object ApiConfig {
    const val BASE_URL: String = "https://defenceless-implausibly-rosamond.ngrok-free.dev"

    val LOGIN_URL: String = BASE_URL + "/api/Login/UserAuth"
    val REGISTER_URL: String = BASE_URL + "/api/Register/UserReg"
    val PROFILE_URL: String = BASE_URL + "/api/Profile/GetProfile"
    val DOCTOR_LIST_URL: String = BASE_URL + "/api/Doctor/GetDoctorList"
    val CONSULTATION_URL: String = BASE_URL + "/api/Doctor/AddConsultation"
    val CONSULTATIONS_ALL_URL: String = BASE_URL + "/api/Doctor/getAllConsultations"

    fun doctorUrl(id: Int): String {
        return BASE_URL + "/api/Doctor/GetDoctor/" + id
    }

    fun consultationUpdateUrl(id: Int, status: String?): String {
        return BASE_URL + "/api/Doctor/update/" + id + "/" + status
    }
}