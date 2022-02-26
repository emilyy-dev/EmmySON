import io.github.emilyydev.emmyson.conventions.extension.MultiReleaseConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.AbstractMultiReleaseConfig

plugins {
    id("emmyson.licensed")
}

extensions.create(MultiReleaseConfig::class, "multiReleaseConfig", AbstractMultiReleaseConfig::class)
