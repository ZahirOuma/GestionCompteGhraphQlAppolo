plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.apollo)
}

android {
    namespace = "ma.ensa.graphqltp"
    compileSdk = 34

    defaultConfig {
        applicationId = "ma.ensa.graphqltp"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
//apolo
apollo {
    service("banque") {
        packageName.set("ma.ensa.graphqltp")
        schemaFile.set(file("src/main/graphql/schema.graphqls"))
        generateKotlinModels.set(true)
      }
}

dependencies {
    implementation(libs.apollo.runtime)
    implementation (libs.kotlinx.coroutines.android.v171)
    implementation (libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}