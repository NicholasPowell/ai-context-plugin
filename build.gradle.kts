import com.niloda.build.extensions.Extensions.Companion.sources
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("com.niloda.build-extensions") version "1.0-SNAPSHOT"
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "com.niloda.aicontext"
version = "1.0-SNAPSHOT"

repositories {
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://packages.jetbrains.team/maven/p/kpm/public/")
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}


sources {
    val model by create {
        implementation dependsOn "org.jetbrains.kotlin:kotlin-stdlib"
        addToJar()
    }
    val main by getting {
        implementation dependsOn model
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")
        bundledPlugins("com.intellij.java", "org.jetbrains.kotlin")
    }

////    implementation(compose.desktop.currentOs)
//    implementation(compose.desktop.macos_arm64)
//    implementation("org.jetbrains.compose.runtime:runtime:1.7.3") {
//        // Exclude Coroutines to avoid conflicts
//        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
//    }
//    platform("com.intellij:platform-api")
//    implementation("org.jetbrains.compose.desktop:desktop:1.7.3")
//    implementation("org.jetbrains.compose.desktop:desktop-jvm:1.7.3")
//
    listOf(
        compose.desktop.macos_x64,
        compose.desktop.macos_arm64,
        compose.desktop.windows_x64,
        compose.desktop.linux_x64,
        compose.desktop.linux_arm64,
    ).forEach { artifact ->
        implementation(artifact) {
            exclude(group = "org.jetbrains.kotlinx")
            exclude(group = "org.jetbrains.compose.material")
        }
    }
    implementation("org.jetbrains.jewel:jewel-ide-laf-bridge-241:0.27.0") {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("com.darkrockstudios:mpfilepicker:3.1.0") {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("androidx.lifecycle:lifecycle-common-jvm:2.8.7") {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("androidx.lifecycle:lifecycle-runtime-desktop:2.8.7") {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // For Ollama API calls
    implementation("org.json:json:20231013") // For JSONObject
}



tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("243.*")
    }
}