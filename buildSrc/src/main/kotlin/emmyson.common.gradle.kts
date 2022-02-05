plugins {
    `java-library`
}

group = "io.github.emilyy-dev"

repositories {
    mavenCentral()
}

tasks {
    withType<Jar> {
        dependsOn(check)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(9)
    }

    withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
}
