plugins {
  id("com.android.application")
  id("com.google.gms.google-services")
  id("org.jetbrains.kotlin.android")
  id("com.ncorti.ktfmt.gradle") version "0.16.0"
  id("jacoco")
  id("org.sonarqube") version "4.4.1.3373"
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
  kotlin("kapt")
}

secrets {
  // Optionally specify a different file name containing your secrets.
  // The plugin defaults to "local.properties"
  propertiesFileName = "secrets.properties"

  // A properties file containing default secret values. This file can be
  // checked in version control.
  defaultPropertiesFileName = "local.defaults.properties"
}

android {
  namespace = "com.github.se.gatherspot"
  compileSdk = 34
  packagingOptions {
    exclude("META-INF/LICENSE.md")
    exclude("META-INF/LICENSE-notice.md")
  }
  testCoverage { jacocoVersion = "0.8.8" }

  sonar {
    properties {
      property("sonar.projectKey", "GatherSpot_MobileApp")
      property("sonar.projectName", "MobileApp")
      property("sonar.organization", "gatherspot")
      property("sonar.host.url", "https://sonarcloud.io")
      // Comma-separated paths to the various directories containing the *.xml JUnit report files.
      // Each path may be absolute or relative to the project base directory.
      property(
          "sonar.junit.reportPaths",
          "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
      // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will
      // have to be changed too.
      property(
          "sonar.androidLint.reportPaths",
          "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
      // Paths to JaCoCo XML coverage report files.
      property(
          "sonar.coverage.jacoco.xmlReportPaths",
          "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
  }
  defaultConfig {
    applicationId = "com.github.se.gatherspot"
    minSdk = 29
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      enableUnitTestCoverage = true
      enableAndroidTestCoverage = true
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {

  // ---------------------- </IMPLEMENTATION ------------------
  // </ Android COMPOSE
  implementation("androidx.compose.runtime:runtime-livedata")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui:1.4.0")
  implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
  implementation("androidx.compose.material:material")
  implementation("androidx.compose.material3:material3")
  implementation(
      "androidx.compose.material:material:1.6.2") // necessary for EventUI Automirrored to work
  implementation(platform("androidx.compose:compose-bom:2023.08.00"))
  /// >

  // </firebase
  implementation(platform("com.google.firebase:firebase-bom:32.8.1")) // changed to newer version
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-database")
  implementation("com.google.firebase:firebase-firestore")
  implementation("com.google.firebase:firebase-auth")
  implementation("com.firebaseui:firebase-ui-auth:7.2.0") // Can't find it anywhere
  /// >

  // </Used for uploading images
  implementation("com.google.firebase:firebase-storage")
  implementation("androidx.fragment:fragment:1.5.5")
  implementation("com.squareup.okhttp3:okhttp:3.10.0")
  /// >

  // <gson
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.google.code.gson:gson:2.8.6") // DOUBLON
  /// >

  // </Android navigation
  implementation("androidx.navigation:navigation-compose:2.6.0-rc01")
  implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
  implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

  /// >

  // </Google Maps
  implementation("com.google.maps.android:maps-compose:4.3.0")
  implementation("com.google.maps.android:maps-compose-utils:4.3.0")
  /// >

  // </gms play
  implementation("com.google.android.gms:play-services-auth:20.6.0")
  implementation("com.google.android.gms:play-services-maps:18.1.0")
  /// >

  // </coil
  implementation("io.coil-kt:coil-compose:2.6.0")
  /// >

  implementation("androidx.appcompat:appcompat:1.6.1")

  // QR Code
  // ZXing Core
  implementation("com.google.zxing:core:3.5.1")
  // ZXing Android Embedded (for scanning)
  implementation("com.journeyapps:zxing-android-embedded:4.3.0")
  implementation ("androidx.camera:camera-core:1.0.2")
  implementation ("androidx.camera:camera-camera2:1.0.2")
  implementation ("androidx.camera:camera-lifecycle:1.0.2")
  implementation ("androidx.camera:camera-view:1.0.0-alpha29")

  //Barcode
  implementation ("com.google.mlkit:barcode-scanning:17.0.0")

  //Camera Permission
  implementation ("com.google.accompanist:accompanist-permissions:0.19.0")


  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

  implementation("com.squareup.okhttp3:okhttp:3.10.0")
  implementation("com.squareup.okhttp3:okhttp:4.9.0") // For location //DOUBLON

  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
  implementation("com.google.android.play:core-ktx:1.7.0")

  implementation("com.google.android.material:material:1.10.0") // Different than compose:material ?
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  implementation("androidx.compose.ui:ui-graphics")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
  implementation(libs.androidx.core.animation)
  implementation(libs.play.services.location)
    implementation(libs.androidx.room.common)
  implementation(libs.core.ktx)

    // ---------------------- /IMPLEMENTATION> ------------------

  // DEBUG IMPLEMENTATION DEPENDENCIES

  debugImplementation("androidx.compose.ui:ui-tooling:1.4.0")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.0")

  // TEST IMPLEMENTATION DEPENDENCIES

  testImplementation("junit:junit:4.13.2")
  testImplementation("org.mockito:mockito-core:3.11.2")
  testImplementation("org.mockito:mockito-inline:2.13.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")

  // ANDROID TEST IMPLEMENTATION DEPENDENCIES

  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.0")
  // Dependency for using Intents in instrumented tests
  androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))

  androidTestImplementation("com.kaspersky.android-components:kaspresso:1.4.3")
  // Allure support
  androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:1.4.3")
  // Jetpack Compose support
  androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:1.4.1")

  // </ Dependencies for using MockK in instrumented tests
  androidTestImplementation("io.mockk:mockk:1.13.7")
  androidTestImplementation("io.mockk:mockk-android:1.13.7")
  androidTestImplementation("io.mockk:mockk-agent:1.13.7")
  androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  /// >

  androidTestImplementation("org.mockito:mockito-core:3.11.2")
  androidTestImplementation("org.mockito:mockito-inline:2.13.0")
  androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.0")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
  androidTestImplementation("com.kaspersky.android-components:kaspresso:1.4.3")
  // Allure support
  androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:1.4.3")
  // Jetpack Compose support
  androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:1.4.1")
  androidTestImplementation("com.google.firebase:firebase-database")
  androidTestImplementation("com.google.firebase:firebase-firestore")

  // Image fetching library
  implementation("io.coil-kt:coil-compose:2.6.0")
    //</ Room for local storage
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    ///>
}

tasks.withType<Test> {
  // Configure Jacoco for each tests
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
  mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

  reports {
    xml.required = true
    html.required = true
  }

  val fileFilter =
      listOf(
          "**/R.class",
          "**/R$*.class",
          "**/BuildConfig.*",
          "**/Manifest*.*",
          "**/*Test*.*",
          "android/**/*.*",
      )
  val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
  val mainSrc = "${project.projectDir}/src/main/java"

  sourceDirectories.setFrom(files(mainSrc))
  classDirectories.setFrom(files(debugTree))
  executionData.setFrom(
      fileTree(project.buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
      })
}

sonar {
  properties {
    property("sonar.projectKey", "GatherSpot_MobileApp")
    property("sonar.projectName", "MobileApp")
    property("sonar.organization", "gatherspot")
    property("sonar.host.url", "https://sonarcloud.io")
    // Comma-separated paths to the various directories containing the *.xml JUnit report files.
    // Each path may be absolute or relative to the project base directory.
    property(
        "sonar.junit.reportPaths",
        "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
    // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will
    // have to be changed too.
    property(
        "sonar.androidLint.reportPaths",
        "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
    // Paths to JaCoCo XML coverage report files.
    property(
        "sonar.coverage.jacoco.xmlReportPaths",
        "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
  }
}
