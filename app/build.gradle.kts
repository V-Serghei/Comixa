plugins {
    id("comixa.android.application")
    id("comixa.android.compose")
    id("comixa.android.hilt")
}

android {
    namespace = "com.comixa.app"

    defaultConfig {
        applicationId = "com.comixa.app"
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":feature:library"))
    implementation(project(":feature:reader"))
    implementation(project(":feature:settings"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation)
    implementation(libs.core.ktx)
}
