package io.github.emilyydev.emmyson.conventions.extension

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

interface LicenseConfig {

    val licenseFiles: ListProperty<File>
    val licenseTarget: Property<String>
}
