plugins {
    id("comixa.android.library")
    id("comixa.android.compose")
    id("comixa.android.hilt")
}

android {
    namespace = "com.comixa.feature.library"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation(libs.activity.compose)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation)
    implementation(libs.navigation.compose)
}
