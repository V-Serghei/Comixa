
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.comixa.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.comixa.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}
kotlin {
    jvmToolchain(17)
}
dependencies {

    // Room
    implementation("androidx.room:room-runtime:2.8.2")
    implementation ("androidx.room:room-ktx:2.8.2")
    annotationProcessor ("androidx.room:room-compiler:2.8.2")


    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // OkHttp
    implementation ("com.squareup.okhttp3:okhttp:5.2.1")

    // SwipeRefresh
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation(project(":app:data"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.10.5")

    // CameraX
    implementation ("androidx.camera:camera-core:1.5.0")
    implementation ("androidx.camera:camera-camera2:1.5.0")
    implementation ("androidx.camera:camera-lifecycle:1.5.0")
    implementation ("androidx.camera:camera-view:1.5.0")

    // Coil for image loading
    implementation("io.coil-kt:coil:2.7.0")
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    
    // DrawerLayout
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

}
