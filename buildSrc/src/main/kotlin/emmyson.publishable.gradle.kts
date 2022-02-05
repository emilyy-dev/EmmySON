import io.github.emilyydev.emmyson.conventions.extension.PublicationConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.PublicationConfigImpl

plugins {
    `maven-publish`
    signing
    id("emmyson.licensed")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
}

publishing {
    val mavenJava by publications.creating(MavenPublication::class) {
        from(components["java"])

        pom {
            packaging = "jar"
            url.set("https://github.com/emilyy-dev/EmmySON")

            developers {
                developer {
                    id.set("emilyy-dev")
                    url.set("https://github.com/emilyy-dev")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/emilyy-dev/EmmySON.git")
                developerConnection.set("scm:git:ssh://github.com:emilyy-dev/EmmySON.git")
                url.set("https://github.com/emilyy-dev/EmmySON/tree/main")
            }
        }
    }

    val publicationConfig = PublicationConfigImpl(repositories, mavenJava, objects.listProperty(String::class))
    extensions.add(PublicationConfig::class, "publicationConfig", publicationConfig)

    tasks {
        withType<Javadoc> {
            inputs.property("publicationConfig.linkedJavadocs", publicationConfig.linkedJavadocs::get)
            doFirst {
                val standardOptions = options as? StandardJavadocDocletOptions ?: return@doFirst
                standardOptions.links?.addAll(publicationConfig.linkedJavadocs.get())
            }
        }
    }

    signing {
        sign(mavenJava)
    }
}
