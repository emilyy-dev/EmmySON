plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.hierynomus.gradle.plugins", "license-gradle-plugin", "0.16.1")
    implementation("me.champeau.jmh", "jmh-gradle-plugin", "0.6.6")
}

tasks {
    compileJava {
        options.release.set(8)
    }
}
