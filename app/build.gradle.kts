plugins {

        alias(libs.plugins.android.application) // Ya está aquí
        alias(libs.plugins.kotlin.android)      // Ya está aquí
        alias(libs.plugins.kotlin.compose)
        id("com.google.devtools.ksp") version "1.9.23-1.0.19" // Añadiste KSP aquí


// ... (resto del archivo android { ... } y dependencies { ... }) ...
}

android {
    namespace = "com.example.spotfinder"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.spotfinder"
        minSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("io.coil-kt:coil-compose:2.6.0") // Revisa la última versión de Coil Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    val room_version = "2.6.1" // Usa la última versión estable
    implementation("androidx.room:room-runtime:$room_version")

}