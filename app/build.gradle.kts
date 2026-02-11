import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.procrastination"
    compileSdk = 36

    defaultConfig {
        // --- קוד מתוקן ל-Kotlin DSL ---
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        // שליפת המפתח בצורה בטוחה
        val apiKey = properties.getProperty("apiKey") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
        // ------------------------------


        applicationId = "com.example.procrastination"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "b1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("com.google.firebase:firebase-ai:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.cardview)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.pdf.ink)
    implementation(libs.work.runtime)
    implementation(libs.generativeai)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}