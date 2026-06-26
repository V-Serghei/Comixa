plugins {
    `kotlin-dsl`
}

group = "com.comixa.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.build.android.gradle)
    compileOnly(libs.build.kotlin.gradle)
    compileOnly(libs.build.kotlin.compose.gradle)
    compileOnly(libs.build.ksp.gradle)
    compileOnly(libs.build.hilt.gradle)
}

gradlePlugin {
    plugins {
        register("comixaAndroidApplication") {
            id = "comixa.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("comixaAndroidLibrary") {
            id = "comixa.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("comixaAndroidCompose") {
            id = "comixa.android.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("comixaAndroidHilt") {
            id = "comixa.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("comixaKotlinLibrary") {
            id = "comixa.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}
