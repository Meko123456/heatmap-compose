plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates("io.github.meko123456", "heatmap", "0.2.0")

    pom {
        name.set("heatmap-compose")
        description.set("GitHub-style contribution heatmap for Jetpack Compose — Canvas composable plus Bitmap renderer for widgets")
        url.set("https://github.com/Meko123456/heatmap-compose")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("Meko123456")
                name.set("Merab Kochlamazashvili")
                url.set("https://github.com/Meko123456")
            }
        }
        scm {
            url.set("https://github.com/Meko123456/heatmap-compose")
            connection.set("scm:git:git://github.com/Meko123456/heatmap-compose.git")
            developerConnection.set("scm:git:ssh://git@github.com/Meko123456/heatmap-compose.git")
        }
    }
}

android {
    namespace = "io.github.meko123456.heatmap"
    // Deliberately 36 while every app in the fleet is on 37, and not an oversight.
    //
    // AGP writes this straight into the published AAR's metadata as minCompileSdk, so a library's
    // compileSdk is a requirement placed on everyone who depends on it, not a private build detail.
    // Verified rather than assumed: building this module on 37 produces minCompileSdk=37 in
    // heatmap/build/intermediates/aar_metadata/release/. That is the exact mechanism that made
    // Compose BOM 2026.09.00 and okhttp 5.5.0 break projects across this fleet.
    //
    // This library is on Maven Central and consumed today. Raising it would force every consumer
    // forward to buy nothing, since nothing here needs an API newer than 36. It moves when
    // something in it actually requires a newer platform, and not before.
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    testImplementation(libs.junit)
}
