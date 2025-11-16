pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            // Plugin versions
            version("androidGradlePlugin", "8.1.2")
            version("kotlin", "1.9.0")
            plugin("android.application", "com.android.application").versionRef("androidGradlePlugin")
            plugin("kotlin.android", "org.jetbrains.kotlin.android").versionRef("kotlin")

            // Dependencies
            library("androidx.core.ktx", "androidx.core", "core-ktx").version("1.12.0")
            library("androidx.appcompat", "androidx.appcompat", "appcompat").version("1.6.1")
            library("material", "com.google.android.material", "material").version("1.11.0")
            library("androidx.activity", "androidx.activity", "activity-ktx").version("1.8.2")
            library("androidx.constraintlayout", "androidx.constraintlayout", "constraintlayout").version("2.1.4")
            library("androidx.recyclerview", "androidx.recyclerview", "recyclerview").version("1.3.2")
            library("junit", "junit", "junit").version("4.13.2")
            library("androidx.junit", "androidx.test.ext", "junit").version("1.1.5")
            library("androidx.espresso.core", "androidx.test.espresso", "espresso-core").version("3.5.1")
        }
    }
}

rootProject.name = "SpendSprout_OPSC"
include(":app")
