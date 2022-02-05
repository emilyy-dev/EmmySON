plugins {
    id("emmyson.publishable")
    id("emmyson.testable")
    id("emmyson.multi-release-jar")
}

version = simpleVersion()

fun simpleVersion(): String {
    val apiVersion: String = project(":emmyson-api").version.toString()
    val simpleVersion = "${apiVersion.removeSuffix("-SNAPSHOT")}.0"
    return if (apiVersion.endsWith("-SNAPSHOT")) "$simpleVersion-SNAPSHOT" else simpleVersion
}

licenseConfig.licenseHeader.set(file("license-header.txt"))
multiReleaseConfig.addLanguageVersion(11)

dependencies {
    api(project(":emmyson-api"))
    implementation("net.kyori", "examination-string", "1.3.0")
}

publicationConfig {
    name.set("EmmySON Simple")
    description.set("Simple implementation for the EmmySON API")
    linkedJavadocs.addAll(
        "https://docs.oracle.com/javase/9/docs/api/",
        "https://javadoc.io/doc/org.jetbrains/annotations/23.0.0/",
        "https://javadoc.io/doc/net.kyori/examination-api/1.3.0/"
    )
    license {
        name.set("LGPL-3.0")
        url.set("https://opensource.org/licenses/LGPL-3.0")
    }
}
