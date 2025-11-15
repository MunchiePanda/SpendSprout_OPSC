plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // KSP for annotation processing (Hilt)
    id("com.google.dagger.hilt.android") // Hilt plugin
    id("com.google.gms.google-services") // Google Services plugin
}

android {
    // This namespace MUST match your code and google-services.json
    namespace = "com.example.spendsprout_opsc"
    compileSdk = 34

    defaultConfig {
        // This application ID MUST match the namespace
        applicationId = "com.example.spendsprout_opsc"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true // Enable View Binding
        dataBinding = true // Enable Data Binding
    }
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    // Core Android & UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Coroutines and Lifecycle
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1") // For Firebase suspend functions
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1") // Use a recent version of Hilt
    ksp("com.google.dagger:hilt-compiler:2.51.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0")) // Use a recent BOM
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // --- ROOM HAS BEEN REMOVED ---

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
