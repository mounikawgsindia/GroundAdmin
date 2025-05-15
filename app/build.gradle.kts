plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Required for Hilt
    id("dagger.hilt.android.plugin") // Hilt Plugin
    id("androidx.navigation.safeargs.kotlin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.wingspan.groundowner"
    compileSdk = 35
    buildFeatures {
        buildConfig = true // ✅ Enable BuildConfig generation
    }
    defaultConfig {
        applicationId = "com.wingspan.groundowner"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        debug {

            buildConfigField("String", "BASE_URL", "\"https://pitchnground.com/\"")
        }

        release {


            buildConfigField("String", "BASE_URL","\"https://pitchnground.com/\"")

            isMinifyEnabled = true
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.78")
    implementation ("org.conscrypt:conscrypt-android:2.5.2")
    implementation ("org.openjsse:openjsse:1.1.12")


    implementation("com.facebook.shimmer:shimmer:0.5.0")
    //circular indicator
    implementation ("me.relex:circleindicator:2.1.6")
    implementation("androidx.core:core-splashscreen:1.0.1")
//googleplay service api
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-messaging:24.1.1")
    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    //pinview
    implementation ("io.github.chaosleung:pinview:1.4.4")
    // AndroidX Core & UI
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    // Facebook Shimmer Layout
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Google Play Services (Location)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
