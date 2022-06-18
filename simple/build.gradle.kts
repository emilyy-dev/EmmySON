plugins {
  id("emmyson.publishable")
  id("emmyson.testable")
  id("emmyson.crossdoc")
}

val apiVersion = project(":emmyson-api").version.toString()

version = simpleVersion()

fun simpleVersion(): String {
  val simpleVersion = "${apiVersion.removeSuffix("-SNAPSHOT")}.0"
  return if (apiVersion.endsWith("-SNAPSHOT")) "$simpleVersion-SNAPSHOT" else simpleVersion
}

license.header = file("license-header.txt")
licenseConfig.licenseFiles.addAll(provider {
  listOf(
    layout.projectDirectory.file("COPYING"),
    layout.projectDirectory.file("COPYING.LESSER")
  )
})
crossdocConfig.linkJavadocFor(project(":emmyson-api"))

dependencies {
  api(project(":emmyson-api"))
  implementation("net.kyori", "examination-string", "1.3.0")
}

publicationConfig {
  name.set("EmmySON Simple")
  description.set("Simple implementation for the EmmySON API")
  linkedJavadoc.addAll(
    "https://docs.oracle.com/javase/11/docs/api/",
    "https://javadoc.io/static/org.jetbrains/annotations/23.0.0/",
    "https://javadoc.io/static/net.kyori/examination-api/1.3.0/"
  )
  license {
    name.set("LGPL-3.0")
    url.set("https://opensource.org/licenses/LGPL-3.0")
  }

  val isSnapshot = version.toString().endsWith("-SNAPSHOT")
  val username = findProperty("ossrh.user") as? String ?: return@publicationConfig
  val password = findProperty("ossrh.password") as? String ?: return@publicationConfig
  configureRepo(isSnapshot, username, password)
}
