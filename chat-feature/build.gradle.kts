plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = main.java.ProjectConfig.compileSdk

    defaultConfig {
        minSdk = main.java.ProjectConfig.minSdk
        targetSdk = main.java.ProjectConfig.targetSdk

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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    api(project(":chat-data"))
    implementation(project(":base-feature"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    addAndroidCore()
    addAndroidUI()

    addKotlin()

    addGlide()

    addNavigation()
    addRoom()
    addWorkManger()

    addLifeCycle()

    addHilt()
}