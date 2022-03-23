import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import io.github.emilyydev.emmyson.conventions.extension.LicenseConfig

plugins {
    id("emmyson.common")
    id("com.github.hierynomus.license-base")
}

val licenseConfig = extensions.create("licenseConfig", LicenseConfig::class)
with(licenseConfig) {
    licenseFiles.convention(provider { listOf(file("LICENSE.txt")) })
    licenseTarget.convention(provider { "$group/$name" })
}

license {
    header = file("LICENSE.txt")
    encoding = Charsets.UTF_8.name()
    mapping("java", "DOUBLESLASH_STYLE")
    include("**/*.java")
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

    withType<Jar> {
        inputs.property("licenseConfig.licenseFiles", licenseConfig.licenseFiles)
        inputs.property("licenseConfig.licenseTarget", licenseConfig.licenseTarget)

        metaInf {
            into(licenseConfig.licenseTarget) {
                filteringCharset = Charsets.UTF_8.name()
                from(licenseConfig.licenseFiles)
            }
        }
    }
}
