import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import io.github.emilyydev.emmyson.conventions.extension.LicenseConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.LicenseConfigImpl
import nl.javadude.gradle.plugins.license.License

plugins {
    id("emmyson.common")
    id("com.github.hierynomus.license-base")
}

val licenseConfig = LicenseConfigImpl(
    license,
    objects.fileProperty().convention { file("LICENSE.txt") },
    objects.fileProperty().convention { file("LICENSE.txt") },
    objects.property(String::class).convention(providers.provider { "$group/$name" })
)
extensions.add(LicenseConfig::class, "licenseConfig", licenseConfig)

license {
    encoding = Charsets.UTF_8.name()
    mapping("java", "DOUBLESLASH_STYLE")
    include("**/*.java")
}

fun License.configureLicenseTask() {
    inputs.property("licenseConfig.licenseHeader", licenseConfig.licenseHeader.asFile::get)
    conventionMapping("header", licenseConfig.licenseHeader.asFile::get)
}

tasks {
    register("licenseFormatAll") {
        withType<LicenseFormat>().forEach {
            finalizedBy(it)
        }
    }

    val licenseCheckAll by registering(DefaultTask::class) {
        withType<LicenseCheck>().forEach {
            finalizedBy(it)
        }
    }

    check {
        dependsOn(licenseCheckAll)
    }

    withType<LicenseCheck> { configureLicenseTask() }
    withType<LicenseFormat> { configureLicenseTask() }

    withType<Jar> {
        inputs.property("licenseConfig.licenseFile", licenseConfig.licenseFile.asFile::get)
        inputs.property("licenseConfig.licenseTarget", licenseConfig.licenseTarget::get)
        metaInf {
            from(licenseConfig.licenseFile) {
                into(licenseConfig.licenseTarget)
            }
        }
    }
}
