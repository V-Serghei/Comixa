plugins {
    id("comixa.android.library")
    id("comixa.android.compose")
}

android {
    namespace = "com.comixa.core.ui"
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)

    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.preview)
    api(libs.compose.material3)
    api(libs.coil.compose)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
