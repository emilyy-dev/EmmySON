package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface LicenseConfig {

    val licenseFile: RegularFileProperty
    val licenseTarget: Property<String>
}
