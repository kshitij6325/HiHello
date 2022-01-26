plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":base-data"))

    addKotlin()

    addFirebaseDatabase()
    addFirebaseStorage()

    addHilt()

    addJunit()
}