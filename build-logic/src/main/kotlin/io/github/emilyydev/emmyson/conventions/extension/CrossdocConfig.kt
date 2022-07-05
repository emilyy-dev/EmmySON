package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.Project

interface CrossdocConfig {

    fun linkJavadocFor(project: Project)
}
