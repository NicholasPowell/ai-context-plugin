dependencyResolutionManagement {
    versionCatalogs {
        create("libs2") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}


rootProject.name = "ai-context-plugin"