package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomLicense

interface PublicationConfig {

    val name: Property<String>
    val description: Property<String>
    val linkedJavadocs: ListProperty<String>

    fun license(action: Action<in MavenPomLicense>)

    fun configureRepo(isSnapshot: Boolean, username: String, password: String)
}
