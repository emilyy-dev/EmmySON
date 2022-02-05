plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.hierynomus.gradle.plugins", "license-gradle-plugin", "0.16.1")
}

tasks {
    withType<JavaCompile> {
        options.release.set(8)
    }
}