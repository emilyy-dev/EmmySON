package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.LicenseConfig
import nl.javadude.gradle.plugins.license.LicenseExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import java.io.File

class LicenseConfigImpl(
    private val license: LicenseExtension,
    override val licenseHeader: RegularFileProperty,
    override val licenseFile: RegularFileProperty,
    override val licenseTarget: Property<String>
) : LicenseConfig