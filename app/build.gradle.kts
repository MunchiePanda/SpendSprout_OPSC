plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    //room implementation
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.spendsprout_opsc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.spendsprout_opsc"
        minSdk = 26
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
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xallow-any-scripts-in-source-roots",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xmetadata-version=1.6",
            "-Xskip-prerelease-check",
            "-Xsuppress-version-warnings"
        )
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.junit.jupiter)

    // Core testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // AndroidX Test dependencies
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    
    // JUnit dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")

    //room implementation
    val room_version = "2.7.2"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // Room testing dependencies
    testImplementation("androidx.room:room-testing:$room_version")
    androidTestImplementation("androidx.room:room-testing:$room_version")
    
    // Hilt dependency injection - using stable version
    implementation("com.google.dagger:hilt-android:2.44")
    ksp("com.google.dagger:hilt-android-compiler:2.44")
    
    // Additional Hilt support for better metadata handling
    implementation("com.google.dagger:hilt-core:2.44")
    
            // Kotlin metadata compatibility - using stable versions
            implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
            implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            
            // JSON serialization for data persistence
            implementation("com.google.code.gson:gson:2.10.1")
    
    // Additional Kotlin support for Hilt
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    
    // Hilt testing dependencies
    testImplementation("com.google.dagger:hilt-android-testing:2.44")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44")
    kspTest("com.google.dagger:hilt-android-compiler:2.44")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.44")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // ViewModel testing dependencies
    testImplementation("androidx.lifecycle:lifecycle-testing:2.7.0")
    androidTestImplementation("androidx.lifecycle:lifecycle-testing:2.7.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Coroutines testing dependencies
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    
    // Navigation testing dependencies
    testImplementation("androidx.navigation:navigation-testing:2.7.6")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
}