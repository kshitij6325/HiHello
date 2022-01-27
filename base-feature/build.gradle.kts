plugins {
    id("com.android.library")
    id("kotlin-android")
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    addKotlin()
    addAndroidCore()
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    addLifeCycle()

    addNavigation()
}