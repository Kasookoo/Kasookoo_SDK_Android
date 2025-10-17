plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish") // ADD THIS
}

android {
    namespace = "com.app.kasookosdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                groupId = 'com.github.Kasookoo' // Replace with your GitHub username
                artifactId = 'KasookoSDK'                         // SDK name
                version = '1.0'                            // Version

                // This points to your AAR file
                artifact(/libs/KasookoSDK.aar")
            }
        }
    }
}
