package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import java.io.File

interface LicenseConfig {

    val licenseHeader: RegularFileProperty
    val licenseFile: RegularFileProperty
    val licenseTarget: Property<String>
}
