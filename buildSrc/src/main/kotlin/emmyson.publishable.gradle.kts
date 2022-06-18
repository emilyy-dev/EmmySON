import io.github.emilyydev.emmyson.conventions.extension.PublicationConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.AbstractPublicationConfig

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

  val publicationConfig = extensions.create(
    PublicationConfig::class,
    "publicationConfig",
    AbstractPublicationConfig::class,
    repositories,
    mavenJava
  )

  tasks {
    withType<Javadoc> {
      inputs.property("publicationConfig.linkedJavadoc", publicationConfig.linkedJavadoc)
      doFirst {
        val standardOptions = options as? StandardJavadocDocletOptions ?: return@doFirst
        standardOptions.links?.addAll(publicationConfig.linkedJavadoc.get())
      }
    }
  }

  signing {
    useGpgCmd()
    sign(mavenJava)
  }
}
