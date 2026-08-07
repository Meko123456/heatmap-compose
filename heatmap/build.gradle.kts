plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates("io.github.meko123456", "heatmap", "0.1.0")

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
