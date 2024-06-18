plugins {
    //alias(libs.plugins.androidApplication)
    id ("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.br.bancodigital"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.br.bancodigital"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")
    // Add the dependency for the Cloud Storage library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-storage")
    // https://github.com/BlackCaT27/CurrencyEditText
    implementation ("com.github.BlacKCaT27:CurrencyEditText:2.0.2")
    // https://github.com/square/picasso
    implementation ("com.squareup.picasso:picasso:2.8")
    //https://github.com/santalu/maskara
    implementation ("com.github.santalu:maskara:1.0.0")
    //https://github.com/tsuryo/Swipeable-RecyclerView
    implementation ("com.github.tsuryo:Swipeable-RecyclerView:1.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")



}