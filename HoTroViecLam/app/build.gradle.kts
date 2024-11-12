import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11
import org.apache.tools.ant.util.JavaEnvUtils.VERSION_1_8

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.hotrovieclam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hotrovieclam"
        minSdk = 24
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
    buildFeatures{ viewBinding = true}
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.firebase:firebase-database:20.0.5")
    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation ("com.google.firebase:firebase-storage:20.1.0")
    implementation ("org.jsoup:jsoup:1.15.3")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-location:latest-version")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")



    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.material:material:1.4.0")




    // Kiểm tra phiên bản mới nhất

}