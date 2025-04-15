import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    java
//    id("com.niloda.build-extensions") version "1.0-SNAPSHOT"

    alias(libs2.plugins.composeDesktop)
    alias(libs2.plugins.compose.compiler)
    alias(libs2.plugins.ideaPlugin)
    alias(libs2.plugins.kotlinJvm)
//
//    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
//
//    id("org.jetbrains.intellij.platform") version "2.3.0"
//
//
//    id("org.jetbrains.kotlin.jvm") version "2.1.0"
//    id("org.jetbrains.compose") version "1.7.3"
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

//
//sources {
//    val model by create {
//        implementation dependsOn "org.jetbrains.kotlin:kotlin-stdlib"
//        addToJar()
//    }
//    val main by getting {
//        implementation dependsOn model
//    }
//}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3.4")
        bundledPlugins("com.intellij.java", "org.jetbrains.kotlin")
        instrumentationTools()
    }

    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation(compose.desktop.macos_arm64) {
        exclude(group = "org.jetbrains.compose.material")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("org.jetbrains.jewel:jewel-ide-laf-bridge-243:0.27.0") { exclude(group = "org.jetbrains.kotlinx") }
    implementation("com.darkrockstudios:mpfilepicker:3.1.0") { exclude(group = "org.jetbrains.kotlinx") }
    implementation("androidx.lifecycle:lifecycle-common-jvm:2.8.7"){ exclude(group = "org.jetbrains.kotlinx") }
    implementation("androidx.lifecycle:lifecycle-runtime-desktop:2.8.7"){ exclude(group = "org.jetbrains.kotlinx") }
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // For Ollama API calls
    implementation("org.json:json:20231013") // For JSONObject
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
}


tasks {
    test {
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            incremental = false
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