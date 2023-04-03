plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lsy.framelib"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.defaultConfig.minSdk
        targetSdk = AndroidConfig.defaultConfig.targetSdk

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(ThirdLibs.core_ktx)
    implementation(ThirdLibs.ktx_coroutines)

    implementation(ThirdLibs.appcompat)
    implementation(ThirdLibs.material)
    implementation(ThirdLibs.viewmodel_ktx)

    implementation(ThirdLibs.retrofit2)
    implementation(ThirdLibs.logging_interceptor)
    implementation(ThirdLibs.converter_gson)

    implementation(ThirdLibs.gson)

    implementation(ThirdLibs.glide)

    implementation(ThirdLibs.timber)
}