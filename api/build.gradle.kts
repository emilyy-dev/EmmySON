plugins {
    id("emmyson.publishable")
}

version = "1.0-SNAPSHOT"

dependencies {
    api("org.jetbrains", "annotations", "23.0.0")
    api("net.kyori", "examination-api", "1.3.0")
    implementation("net.kyori", "examination-string", "1.3.0")
}

publicationConfig {
    name.set("EmmySON API")
    description.set("EmmySON API - pronounced *emission* - is a JSON parser API, because the world has too many")
    linkedJavadocs.addAll(
        "https://docs.oracle.com/javase/9/docs/api/",
        "https://javadoc.io/doc/org.jetbrains/annotations/23.0.0/",
        "https://javadoc.io/doc/net.kyori/examination-api/1.3.0/"
    )
    license {
        name.set("MIT")
        url.set("https://opensource.org/licenses/mit-license.php")
    }

    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    val username = findProperty("ossrh.user") as? String ?: return@publicationConfig
    val password = findProperty("ossrh.password") as? String ?: return@publicationConfig
    configureRepo(isSnapshot, username, password)
}
