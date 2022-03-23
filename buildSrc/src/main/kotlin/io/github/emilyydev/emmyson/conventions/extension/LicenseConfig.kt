package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface LicenseConfig {

    val licenseFiles: ListProperty<RegularFile>
    val licenseTarget: Property<String>
}
