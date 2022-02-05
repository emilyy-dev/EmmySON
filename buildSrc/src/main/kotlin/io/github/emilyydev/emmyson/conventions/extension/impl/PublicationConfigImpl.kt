package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.PublicationConfig
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPublication

class PublicationConfigImpl(
    private val publication: MavenPublication,
    override val linkedJavadocs: ListProperty<String>
) : PublicationConfig {

    override val name: Property<String>
        get() = publication.pom.name
    override val description: Property<String>
        get() = publication.pom.description

    override fun license(action: Action<in MavenPomLicense>) = publication.pom.licenses { license(action) }
}