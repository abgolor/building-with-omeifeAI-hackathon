import java.util.Properties

/**
 * MIT License
 *
 * Copyright (c) 2025 Eko Translate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * build.gradle for EkoTranslate application
 *
 * This build file configures the Android application build process, including:
 * - Plugin management for Kotlin, Compose, and Hilt
 * - Android configuration (SDK versions, application ID, etc.)
 * - Dependencies for Jetpack Compose, networking, security, and other libraries
 * - Secure API key management through external properties
 */
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    kotlin("kapt")
}

android {
    namespace = "com.ajaytechsolutions.ekotranslate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ajaytechsolutions.ekotranslate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configure BuildConfig fields for secure API key access
        buildConfigField("String", "API_KEY", getApiKey())
    }

    buildTypes {
        release {
            // Enable code shrinking, obfuscation, and optimization for release builds
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor) // Use your version

    // Data parsing
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.converter.moshi)

    // Image loading
    implementation(libs.coil.compose)

    // UI utilities
    implementation(libs.accompanist.systemuicontroller)
    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)

    // Dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Background processing
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // Security - critical for secure API key storage
    implementation(libs.androidx.security.crypto)

    // Logging
    implementation(libs.timber)

    implementation("androidx.core:core-splashscreen:1.0.1")
}

/**
 * Securely retrieves the API key from external sources.
 *
 * This method attempts to load the API key from multiple sources in the following order:
 * 1. local.properties file (for development)
 * 2. Environment variables (for CI/CD and production)
 * 3. Fallback to a placeholder (for development only)
 *
 * To use in development:
 * - Add "API_KEY=your_actual_key" to your local.properties file
 *
 * To use in CI/CD:
 * - Set the EKOTRANSLATE_API_KEY environment variable
 *
 * @return A properly formatted string containing the API key for BuildConfig
 */
fun getApiKey(): String {
    var apiKey: String? = null

    // First try to get from local.properties
    val localPropertiesFile = File(rootProject.projectDir, "local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties()
        localPropertiesFile.inputStream().use { stream ->
            properties.load(stream)
        }
        apiKey = properties.getProperty("API_KEY")
    }

    // If not found, try environment variable
    if (apiKey == null) {
        apiKey = System.getenv("EKOTRANSLATE_API_KEY")
    }

    // Fallback (for development only, not recommended for production)
    if (apiKey == null) {
        apiKey = "PLACEHOLDER_API_KEY"
        println("WARNING: Using placeholder API key. Set the actual key in local.properties or as an environment variable.")
    }

    return "\"${apiKey}\""
}