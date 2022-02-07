package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.PublicationConfig
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

class PublicationConfigImpl(
    private val repositories: RepositoryHandler,
    private val publication: MavenPublication,
    override val linkedJavadocs: ListProperty<String>
) : PublicationConfig {

    private val stagingRepo = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
    private val snapshotsRepo = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    override val name: Property<String>
        get() = publication.pom.name
    override val description: Property<String>
        get() = publication.pom.description

    override fun license(action: Action<in MavenPomLicense>) = publication.pom.licenses { license(action) }

    // i hate this
    override fun configureRepo(isSnapshot: Boolean, username: String, password: String) {
        repositories.maven {
            url = if (isSnapshot) snapshotsRepo else stagingRepo
            credentials {
                this.username = username
                this.password = password
            }
        }
    }
}
