import com.niloda.build.extensions.Extensions.Companion.sources
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
    id("com.niloda.build-extensions") version "1.0-SNAPSHOT"
}

group = "com.niloda.aicontext"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

sources {
    val model by create
    val main by getting {
        implementation dependsOn model
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // For Ollama API calls
    implementation("org.json:json:20230227") // Add this for JSONObject
}

intellij {
    version.set("2024.1.7")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("243.*")
    }
}