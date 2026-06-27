plugins {
    id("comixa.android.library")
    id("comixa.android.compose")
    id("comixa.android.hilt")
}

android {
    namespace = "com.comixa.feature.reader"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.material.icons.extended)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation)
    implementation(libs.navigation.compose)
    implementation(libs.coil.compose)

    implementation(libs.zip4j)
    implementation(libs.junrar)
}
