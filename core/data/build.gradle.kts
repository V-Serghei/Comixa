plugins {
    id("comixa.android.library")
    id("comixa.android.hilt")
}

android {
    namespace = "com.comixa.core.data"
}

dependencies {
    implementation(project(":core:domain"))

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.coroutines.android)

    // WorkManager (фоновое сканирование)
    implementation(libs.work.runtime)

    // zip4j (CBZ/ZIP)
    implementation(libs.zip4j)

    // Core
    implementation(libs.core.ktx)
}
